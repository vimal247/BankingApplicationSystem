package com.twozo.controller;

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
import com.twozo.service.BankService;

public abstract class BankController {
	
	private BankService BANK_SERVICE = getBankService();		
	
	// Abstract method to be implemented by subclasses	
	public abstract BankService getBankService();	

	/* ----- Account Services ----- */
	public final boolean createAccount(final Account account)
			throws AccountProcessingException, CustomerProcessingException {

		return BANK_SERVICE.createAccount(account);
	}

	public final Account getAccountDetails(final String accountNumber) throws AccountProcessingException {
		
		return  BANK_SERVICE.getAccountDetails(accountNumber);
	} 

	public final boolean deactivateAccount(final String accountNumber) throws AccountProcessingException {

		return BANK_SERVICE.deactivateAccount(accountNumber);
	}

	public final boolean updateAccountType(final String accountNumber, final AccountType accountType)
			throws AccountProcessingException {

		return BANK_SERVICE.updateAccountType(accountNumber, accountType);
	}
	
	public final boolean isAccountAvailableInBranch(final String accountNumber) throws AccountProcessingException {

		return BANK_SERVICE.isAccountAvailableInBranch(accountNumber);
	}

	public final boolean isAccountPresent(final String accountNumber) throws AccountProcessingException {

		return BANK_SERVICE.isAccountPresent(accountNumber);
	}

	/* ----- Customer Services ----- */
	public final Customer getCustomerDetails(final String accountNumber) throws CustomerProcessingException {

		return BANK_SERVICE.getCustomerDetails(accountNumber);
	}

	public final boolean updateCustomerDetail(final String updateData, final String accountNumber,
			final String updateType) throws CustomerProcessingException {

		return BANK_SERVICE.updateCustomerDetail(updateData, accountNumber, updateType);
	}

	/* ----- Inquiry Services ----- */
	public final double enquireBalance(final String accountNumber) throws InquiryProcessingException {

		return BANK_SERVICE.getBalance(accountNumber);
	}

	/* ----- Transaction Services ----- */
	public final boolean depositAmount(final String accountNumber, final double amount)
			throws TransactionProcessingException {

		return BANK_SERVICE.depositAmount(accountNumber, amount);
	}

	public final boolean withdrawAmount(final String accountNumber, final double money)
			throws TransactionProcessingException {

		return BANK_SERVICE.withdraw(accountNumber, money);
	}

	public final boolean transferFunds(final String senderAccountNumber, final double money,
			final String receiverAccountNumber) throws TransactionProcessingException {

		return BANK_SERVICE.transferFunds(senderAccountNumber, money, receiverAccountNumber); 
	}

	public final List<Transaction> getTransactionHistory(final String accountNumber)
			throws TransactionProcessingException {

		return BANK_SERVICE.getTransanctionHistory(accountNumber);
	}

	/* ----- Loan Services ----- */
	public final boolean applyloan(final Loan loan) throws LoanProcessingException {

		return BANK_SERVICE.applyloan(loan);
	}

	public Loan getLoanDetails(final String loanNumber) throws LoanProcessingException {

		return BANK_SERVICE.getLoanDetails(loanNumber);
	}

	public final boolean isLoanNumberPresent(final String loanNumber) throws LoanProcessingException {
		
		return BANK_SERVICE.isLoanNumberPresent(loanNumber);	
	}

	public final boolean updateLoanDetails(final String loanNumber, final String updateDate, final String updateType)
			throws LoanProcessingException {
		
		return BANK_SERVICE.updateLoanDetails(loanNumber, updateDate, updateType);
	}

	public final boolean closeLoan(final String loanNumber, final double payAmount) throws LoanProcessingException {
		
		return BANK_SERVICE.closeLoan(loanNumber, payAmount);
	}

	public final boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
			throws LoanProcessingException {
		
		return BANK_SERVICE.repayLoanAmount(loanNumber, repaymentAmount);
	}
}
