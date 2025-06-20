package com.twozo.service.impl;

import java.time.LocalDateTime;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.dao.CustomerDao;
import com.twozo.dao.LoanDao;
import com.twozo.dao.TransactionDao;
import com.twozo.dao.impl.database.AccountDaoDatabaseImpl;
import com.twozo.dao.impl.database.CustomerDaoDatabaseImpl;
import com.twozo.dao.impl.database.LoanDaoDatabaseImpl;
import com.twozo.dao.impl.database.TransactionDaoDatabaseImpl;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.exception.customer.CustomerNotFoundException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.DuplicateLoanException;
import com.twozo.exception.loan.InvalidLoanAmountException;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.model.Loan;
import com.twozo.service.LoanService;
import com.twozo.service.LoanTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl<T extends Loan> implements LoanService<T> {

	private static final double MIN_BALANCE = 500;

	private LoanDao loanDao;
	private CustomerDao customerDao;
	private AccountDao accountDao;
	private TransactionDao transactionDao;
	private LoanServiceFactory loanServiceFactory;

	@Autowired
	public LoanServiceImpl(final CustomerDaoDatabaseImpl customerDao,  final AccountDaoDatabaseImpl accountDao,
						   final TransactionDaoDatabaseImpl transactionDao, final LoanDaoDatabaseImpl loanDao, final LoanServiceFactory loanServiceFactory){
		this.customerDao = customerDao;
		this.accountDao = accountDao;
		this.transactionDao = transactionDao;
		this.loanDao = loanDao;
		this.loanServiceFactory = loanServiceFactory;
	}

	@Override
	public boolean applyloan(final Loan loan) throws LoanProcessingException {
		try {
			final String accountNumber = loan.getAccountNumber();
			final Customer customer = customerDao.getCustomerByAccountNumber(accountNumber);

			loan.setCustomer(customer);
			final LoanTypeService loanService = loanServiceFactory.getService(loan);

			return loanService.applyLoan(loan) && loanSanction(accountNumber, loan.getLoanAmount());
		} catch (InvalidLoanAmountException | LoanNotEligibleException | LoanNotFoundException | DatabaseException
				 | DuplicateLoanException | LoanProcessingException | AccountNotFoundException | CustomerNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	private boolean loanSanction(final String accountNumber, final double amount) throws LoanProcessingException {
		try {
			final String headOfficeAccountNumber = "IOB-LOAN-SERV";
			final Account account = accountDao.getAccountByNumber(accountNumber);

			final Map<String, Long> transactionIds = transactionDao.transferFunds(headOfficeAccountNumber,
					accountNumber, amount);

			if (transactionIds != null) {
				final long senderTransactionId = transactionIds.get("senderTransactionId");
				final long receiverTransactionId = transactionIds.get("receiverTransactionId");

				return transactionDao.saveAccountTransaction(senderTransactionId, account, amount, LocalDateTime.now(),
						TransactionType.DEBIT, receiverTransactionId)	
						&& transactionDao.saveAccountTransaction(receiverTransactionId, account, amount,
								LocalDateTime.now(), TransactionType.CREDIT, senderTransactionId);
			}
		} catch (InsufficientAmount | DatabaseException | AccountNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean updateLoanDetails(final String loanId, final String updateData, final String updateType)
			throws LoanProcessingException {
		try {
			return loanDao.updateLoanDetails(updateData, loanId, updateType);
		} catch (InvalidCustomerUpdateException | LoanNotFoundException | DuplicateUpdateException | DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public Loan getLoanDetails(final String loanId) throws LoanProcessingException {
		try {
			return loanDao.getLoanDetails(loanId);
		} catch (LoanNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public boolean closeLoan(final String loanNumber, double payAmount) throws LoanProcessingException {
		try {
			final Loan loanDetails = loanDao.getLoanDetails(loanNumber);

			if (loanDetails.getBalanceAmount() == payAmount) {
				if (this.repayLoanAmount(loanNumber, payAmount)) {
					return loanDao.removeLoan(loanNumber);
				}
			}
		} catch (LoanNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
			throws LoanProcessingException {
		try {
			final String headOfficeAccountNumber = "IOB-LOAN-SERV";
			final Loan loanDetails = loanDao.getLoanDetails(loanNumber);
			final Account headOfficeAccount = accountDao.getAccountByNumber(headOfficeAccountNumber);
			final String accountNumber = loanDetails.getAccountNumber();
			final Account account = accountDao.getAccountByNumber(accountNumber);
			
			if (account.getBalance() - repaymentAmount >= MIN_BALANCE) { 
				if (loanDetails.getEmiAmount() == repaymentAmount || loanDetails.getBalanceAmount() == repaymentAmount) {
					final Map<String, Long> transactionIds = transactionDao.transferFunds(accountNumber,
							headOfficeAccountNumber, repaymentAmount);

					if (transactionIds != null) {
						final long senderTransactionId = transactionIds.get("senderTransactionId");
						final long receiverTransactionId = transactionIds.get("receiverTransactionId");
						
						if (loanDao.repayLoanAmount(loanNumber, repaymentAmount)) {
							return transactionDao.saveAccountTransaction(senderTransactionId, account, repaymentAmount,
									LocalDateTime.now(), TransactionType.DEBIT, receiverTransactionId)
									&& transactionDao.saveAccountTransaction(receiverTransactionId, headOfficeAccount,
											repaymentAmount, LocalDateTime.now(), TransactionType.CREDIT, senderTransactionId);
						}
					}
				}
			}
		} catch (InsufficientAmount | LoanNotFoundException | DatabaseException | AccountNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final boolean isLoanNumberPresent(final String loanNumber) throws LoanProcessingException {

		try {
			return loanDao.isLoanNumberPresent(loanNumber);
		} catch (LoanNotFoundException | DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	private LoanServiceImpl() {

	}
}
