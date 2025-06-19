package com.twozo.service.impl;

import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.model.EducationLoan;
import com.twozo.model.HomeLoan;
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;
import com.twozo.service.LoanTypeService;

public class LoanServiceFactory {
	public final static LoanTypeService getService(final Loan loan) throws LoanNotEligibleException {
		if (loan == null) {
			throw new IllegalArgumentException("Loan object cannot be null");
		}
		if (loan instanceof HomeLoan) {
			System.out.println( "homeloan instance..");
			return (LoanTypeService) HomeLoanServiceImpl.getInstance();
		} else if (loan instanceof PersonalLoan) { 
			return (LoanTypeService) PersonalLoanServiceImpl.getInstance();
		} else if (loan instanceof EducationLoan) {
			return (LoanTypeService) EducationLoanServiceImpl.getInstance(); 
		} else { 
			throw new LoanNotEligibleException("Invalid loan type: " + loan.getClass().getSimpleName());
		} 
	}
} 