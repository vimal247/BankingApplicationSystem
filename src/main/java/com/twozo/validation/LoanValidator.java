package com.twozo.validation;

import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.model.Loan;

public interface LoanValidator<T extends Loan> {
	
	boolean validate(final T loan) throws LoanNotEligibleException;
}
