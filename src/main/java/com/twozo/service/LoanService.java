package com.twozo.service;

import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.model.Loan;

public interface LoanService<T extends Loan> {
	boolean applyloan(final T loan) throws LoanProcessingException;

	boolean updateLoanDetails(final String loanNumber, final String updateDate, final String key)
			throws LoanProcessingException;

	Loan getLoanDetails(final String aadharNumber) throws LoanProcessingException;

	boolean isLoanNumberPresent(final String loanNumber) throws LoanProcessingException;

	boolean closeLoan(final String loanNumber, final double amount) throws LoanProcessingException;

	boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
			throws LoanProcessingException;
}
