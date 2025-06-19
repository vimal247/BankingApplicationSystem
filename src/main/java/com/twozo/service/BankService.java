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

public abstract class BankService {

	protected static final AccountService ACCOUNT_SERVICE = AccountServiceImpl.getInstance();
	private static final CustomerService CUSTOMER_SERVICE = CustomerServiceImpl.getInstance();
	private static final InquiryService INQUIRY_SERVICE = InquiryServiceImpl.getInstance();
	private static final TransactionService TRANSACTION_SERVICE = TransactionServiceImpl.getInstance();
	private static final LoanServiceImpl<?> LOAN_SERVICE = LoanServiceImpl.getInstance();

	/* ----- Account Services ----- */
	
	// Abstract method to be implemented by subclasses
	public abstract boolean createAccount(final Account account)
			throws AccountProcessingException, CustomerProcessingException;

	public abstract boolean isAccountAvailableInBranch(final String accountNumber) throws AccountProcessingException;

	
	public final Account getAccountDetails(final String accountNumber) throws AccountProcessingException {
	
		return ACCOUNT_SERVICE.getAccountDetails(accountNumber);
	}

	public final boolean updateAccountType(final String accountNumber, final AccountType accountType)
			throws AccountProcessingException {
		
		return ACCOUNT_SERVICE.updateAccountType(accountNumber, accountType);
	}

	public final boolean deactivateAccount(final String accountNumber) throws AccountProcessingException {
		
		return ACCOUNT_SERVICE.deactivateAccount(accountNumber);
	}
	
	public final boolean isAccountPresent(final String accountNumber) throws AccountProcessingException {

		return ACCOUNT_SERVICE.isAccountPresent(accountNumber);
	}

	/* ----- Customer Services ----- */
	public final Customer getCustomerDetails(final String accountNumber) throws CustomerProcessingException {
		
		return CUSTOMER_SERVICE.getCustomerByAccountNumber(accountNumber);
	}

	public boolean updateCustomerDetail(final String updateData, final String accountNumber, final String updateType)
			throws CustomerProcessingException {
		
		return CUSTOMER_SERVICE.updateCustomerDetail(updateData, accountNumber, updateType);
	}

	/* ----- Inquiry Services ----- */
	public final double getBalance(final String accountNumber) throws InquiryProcessingException {
		
		return INQUIRY_SERVICE.getBalance(accountNumber);
	}

	/* ----- Transaction Services ----- */
	public final boolean depositAmount(final String accountNumber, final double amount)
			throws TransactionProcessingException {
		return TRANSACTION_SERVICE.depositAmount(accountNumber, amount);
	}

	public final boolean withdraw(final String accountNumber, final double money)
			throws TransactionProcessingException {
		return TRANSACTION_SERVICE.withdrawAmount(accountNumber, money);
	}

	public final boolean transferFunds(final String senderAccountNumber, final double money,
			final String receiverAccountNumber) throws TransactionProcessingException {
		return TRANSACTION_SERVICE.transferFunds(senderAccountNumber, money, receiverAccountNumber);
	}

	public final List<Transaction> getTransanctionHistory(final String accountNumber)
			throws TransactionProcessingException {
		return TRANSACTION_SERVICE.getTransactionHistory(accountNumber);
	}

	/* ----- Loan Services ----- */
	public final boolean applyloan(final Loan loan)throws LoanProcessingException {
		
		return LOAN_SERVICE.applyloan(loan);
	}

	public Loan getLoanDetails(final String loanNumber) throws LoanProcessingException {
		
		return LOAN_SERVICE.getLoanDetails(loanNumber);
	}  

	public final boolean isLoanNumberPresent(final String loanNumber) throws LoanProcessingException{
		
		return LOAN_SERVICE.isLoanNumberPresent(loanNumber);
	}

	public final boolean updateLoanDetails(final String loanNumber, final String updateDate, final String updateType)
			throws LoanProcessingException {
		
		return LOAN_SERVICE.updateLoanDetails(loanNumber, updateDate, updateType);
	}

	public final boolean closeLoan(final String loanNumber, final double payAmount)
			throws LoanProcessingException {
		
		return LOAN_SERVICE.closeLoan(loanNumber, payAmount);
	}

	public final boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
			throws LoanProcessingException {
		
		return LOAN_SERVICE.repayLoanAmount(loanNumber, repaymentAmount);
	}
}