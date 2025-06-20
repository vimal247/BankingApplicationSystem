package com.twozo.service.impl;

import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.model.EducationLoan;
import com.twozo.model.HomeLoan;
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;
import com.twozo.service.LoanTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceFactory {

	private final HomeLoanServiceImpl homeLoanService;
	private final PersonalLoanServiceImpl personalLoanService;
	private final EducationLoanServiceImpl educationLoanService;

	@Autowired
	public LoanServiceFactory(final HomeLoanServiceImpl homeLoanService,
							  final PersonalLoanServiceImpl personalLoanService, final EducationLoanServiceImpl educationLoanService){
		this.homeLoanService = homeLoanService;
		this.personalLoanService = personalLoanService;
		this.educationLoanService = educationLoanService;
	}

	public LoanTypeService getService(final Loan loan) throws LoanNotEligibleException {
		if (loan == null) {
			throw new IllegalArgumentException("Loan object cannot be null");
		}
		if (loan instanceof HomeLoan) {
			return homeLoanService;
		} else if (loan instanceof PersonalLoan) { 
			return personalLoanService;
		} else if (loan instanceof EducationLoan) {
			return educationLoanService;
		} else { 
			throw new LoanNotEligibleException("Invalid loan type: " + loan.getClass().getSimpleName());
		} 
	}
} 