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
import com.twozo.exception.account.InsufficientAmount;
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

public class LoanServiceImpl<T extends Loan> implements LoanService<T> {

	private static LoanServiceImpl<Loan> instance;

	private static final double MIN_BALANCE = 500;

	private static final LoanDao LOAN_DAO = LoanDaoDatabaseImpl.getInstance();
	private static final CustomerDao CUSTOMER_DAO = CustomerDaoDatabaseImpl.getInstance();
	private static final AccountDao ACCOUNT_DAO = AccountDaoDatabaseImpl.getInstance();
	private static final TransactionDao TRANSACTION_DAO = TransactionDaoDatabaseImpl.getInstance();

	@Override
	public boolean applyloan(final Loan loan) throws LoanProcessingException {
		try {
			final String accountNumber = loan.getAccountNumber();
			final Customer customer = CUSTOMER_DAO.getCustomerByAccountNumber(accountNumber);

			loan.setCustomer(customer);
			final LoanTypeService loanService = LoanServiceFactory.getService(loan);

			return loanService.applyLoan(loan) && loanSanction(accountNumber, loan.getLoanAmount());
		} catch (InvalidLoanAmountException | LoanNotEligibleException | LoanNotFoundException | DatabaseException
				| DuplicateLoanException | LoanProcessingException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	private final boolean loanSanction(final String accountNumber, final double amount) throws LoanProcessingException {
		try {
			final String headOfficeAccountNumber = "IOB-LOAN-SERV";
			final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);

			final Map<String, Long> transactionIds = TRANSACTION_DAO.transferFunds(headOfficeAccountNumber,
					accountNumber, amount);

			if (transactionIds != null) {
				final long senderTransactionId = transactionIds.get("senderTransactionId");
				final long receiverTransactionId = transactionIds.get("receiverTransactionId");

				return TRANSACTION_DAO.saveAccountTransaction(senderTransactionId, account, amount, LocalDateTime.now(),
						TransactionType.DEBIT, receiverTransactionId)	
						&& TRANSACTION_DAO.saveAccountTransaction(receiverTransactionId, account, amount,
								LocalDateTime.now(), TransactionType.CREDIT, senderTransactionId);
			}
		} catch (InsufficientAmount | DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean updateLoanDetails(final String loanId, final String updateData, final String updateType)
			throws LoanProcessingException {
		try {
			return LOAN_DAO.updateLoanDetails(updateData, loanId, updateType);
		} catch (InvalidCustomerUpdateException | DuplicateUpdateException | DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public Loan getLoanDetails(final String loanId) throws LoanProcessingException {
		try {
			return LOAN_DAO.getLoanDetails(loanId);
		} catch (DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public boolean closeLoan(final String loanNumber, double payAmount) throws LoanProcessingException {
		try {
			final Loan loanDetails = LOAN_DAO.getLoanDetails(loanNumber);

			if (loanDetails.getBalanceAmount() == payAmount) {
				if (this.repayLoanAmount(loanNumber, payAmount)) {
					return LOAN_DAO.removeLoan(loanNumber);
				}
			}
		} catch (DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
			throws LoanProcessingException {
		try {
			final String headOfficeAccountNumber = "IOB-LOAN-SERV";
			final Loan loanDetails = LOAN_DAO.getLoanDetails(loanNumber);
			final Account headOfficeAccount = ACCOUNT_DAO.getAccountByNumber(headOfficeAccountNumber);
			final String accountNumber = loanDetails.getAccountNumber();
			final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);
			
			if (account.getBalance() - repaymentAmount >= MIN_BALANCE) { 
				if (loanDetails.getEmiAmount() == repaymentAmount || loanDetails.getBalanceAmount() == repaymentAmount) {
					final Map<String, Long> transactionIds = TRANSACTION_DAO.transferFunds(accountNumber,
							headOfficeAccountNumber, repaymentAmount);

					if (transactionIds != null) {
						final long senderTransactionId = transactionIds.get("senderTransactionId");
						final long receiverTransactionId = transactionIds.get("receiverTransactionId");
						
						if (LOAN_DAO.repayLoanAmount(loanNumber, repaymentAmount)) {
							return TRANSACTION_DAO.saveAccountTransaction(senderTransactionId, account, repaymentAmount,
									LocalDateTime.now(), TransactionType.DEBIT, receiverTransactionId)
									&& TRANSACTION_DAO.saveAccountTransaction(receiverTransactionId, headOfficeAccount,
											repaymentAmount, LocalDateTime.now(), TransactionType.CREDIT, senderTransactionId);
						}
					}
				}
			}
		} catch (InsufficientAmount | DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final boolean isLoanNumberPresent(final String loanNumber) throws LoanProcessingException {

		try {
			return LOAN_DAO.isLoanNumberPresent(loanNumber);
		} catch (LoanNotFoundException | DatabaseException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}	

	public static final LoanServiceImpl<?> getInstance() {
		if (instance == null) {
			instance = new LoanServiceImpl<Loan>();
		}
		return instance;
	}

	private LoanServiceImpl() {

	}
}
