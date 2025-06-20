package com.twozo.service;

import java.util.List;

import com.twozo.enums.AccountType;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.inquiry.InquiryProcessingException;
import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.exception.transaction.TransactionProcessingException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.model.Loan;
import com.twozo.model.Transaction;
import com.twozo.service.impl.AccountServiceImpl;
import com.twozo.service.impl.CustomerServiceImpl;
import com.twozo.service.impl.InquiryServiceImpl;
import com.twozo.service.impl.LoanServiceImpl;
import com.twozo.service.impl.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class BankService {

	private AccountServiceImpl accountService;
	private CustomerServiceImpl customerService;
	private InquiryServiceImpl inquiryService;
	private TransactionServiceImpl transactionService;
	private LoanServiceImpl<?> loanService;

	@Autowired
	public BankService( final AccountServiceImpl accountService,
			final CustomerServiceImpl customerService,
			final InquiryServiceImpl inquiryService,
			final TransactionServiceImpl transactionService,
			final LoanServiceImpl<?> loanService) {
		this.accountService = accountService;
		this.customerService = customerService;
		this.inquiryService = inquiryService;
		this.transactionService = transactionService;
		this.loanService = loanService;
	}

	public BankService() {

	}

	/* ----- Account Services ----- */
	
	// Abstract method to be implemented by subclasses
	public abstract boolean createAccount(final Account account)
			throws AccountProcessingException, CustomerProcessingException;

	public abstract boolean isAccountPresentInBranch(final String accountNumber) throws AccountProcessingException;

	
	public final Account getAccountDetails(final String accountNumber) throws AccountProcessingException {
	
		return accountService.getAccountDetails(accountNumber);
	}

	public final boolean updateAccountType(final String accountNumber, final AccountType accountType)
			throws AccountProcessingException {
		
		return accountService.updateAccountType(accountNumber, accountType);
	}

	public final boolean deactivateAccount(final String accountNumber) throws AccountProcessingException {
		
		return accountService.deactivateAccount(accountNumber);
	}
	
	public final boolean isAccountAvailable(final String accountNumber) throws AccountProcessingException {

		return accountService.isAccountAvailable(accountNumber);
	}

	/* ----- Customer Services ----- */
	public final Customer getCustomerDetails(final String accountNumber) throws CustomerProcessingException {
		
		return customerService.getCustomerByAccountNumber(accountNumber);
	}

	public boolean updateCustomerDetail(final String updateData, final String accountNumber, final String updateType)
			throws CustomerProcessingException {
		
		return customerService.updateCustomerDetail(updateData, accountNumber, updateType);
	}

	/* ----- Inquiry Services ----- */
	public final double getBalance(final String accountNumber) throws InquiryProcessingException {
		
		return inquiryService.getBalance(accountNumber);
	}

	/* ----- Transaction Services ----- */
	public final boolean depositAmount(final String accountNumber, final double amount)
			throws TransactionProcessingException {
		return transactionService.depositAmount(accountNumber, amount);
	}

	public final boolean withdraw(final String accountNumber, final double money)
			throws TransactionProcessingException {
		return transactionService.withdrawAmount(accountNumber, money);
	}

	public final boolean transferFunds(final String senderAccountNumber, final double money,
			final String receiverAccountNumber) throws TransactionProcessingException {
		return transactionService.transferFunds(senderAccountNumber, money, receiverAccountNumber);
	}

	public final List<Transaction> getTransanctionHistory(final String accountNumber)
			throws TransactionProcessingException {
		return transactionService.getTransactionHistory(accountNumber);
	}

	/* ----- Loan Services ----- */
	public final boolean applyloan(final Loan loan)throws LoanProcessingException {
		
		return loanService.applyloan(loan);
	}

	public Loan getLoanDetails(final String loanNumber) throws LoanProcessingException {
		
		return loanService.getLoanDetails(loanNumber);
	}  

	public final boolean isLoanNumberPresent(final String loanNumber) throws LoanProcessingException{
		
		return loanService.isLoanNumberPresent(loanNumber);
	}

	public final boolean updateLoanDetails(final String loanNumber, final String updateDate, final String updateType)
			throws LoanProcessingException {
		
		return loanService.updateLoanDetails(loanNumber, updateDate, updateType);
	}

	public final boolean closeLoan(final String loanNumber, final double payAmount)
			throws LoanProcessingException {
		
		return loanService.closeLoan(loanNumber, payAmount);
	}

	public final boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
			throws LoanProcessingException {
		
		return loanService.repayLoanAmount(loanNumber, repaymentAmount);
	}
}