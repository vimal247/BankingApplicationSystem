package com.twozo.service;

import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.DuplicateLoanException;
import com.twozo.exception.loan.InvalidLoanAmountException;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.model.Loan;

public interface LoanTypeService {	
	<T extends Loan> boolean applyLoan(final T loan) throws InvalidLoanAmountException, LoanNotEligibleException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException;
}
 