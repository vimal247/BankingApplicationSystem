package com.twozo.validation;

import java.time.LocalDate;
import java.time.Period;

import com.twozo.enums.LoanStatus;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.model.EducationLoan;
import com.twozo.model.Loan;

public class EducationLoanValidator implements LoanValidator<EducationLoan> {
	
	private static EducationLoanValidator instance; 

	@Override
	public boolean validate(final EducationLoan educationLoan) throws LoanNotEligibleException { 
		return checkEligibleForLoan(educationLoan);	
	} 

	@SuppressWarnings("unused")
	private static boolean checkEligibleForLoan(final Loan loan) throws LoanNotEligibleException { 
		final int months = loan.getLoanTenure().getYears() * 12;
		final double interestRate = loan.getInterestRate();
		final double loanAmount = loan.getLoanAmount() * (1 + (interestRate / 100));
		final double totalRepayableAmount = loanAmount * (1 + (interestRate / 100));
		final int age = calculateAge(loan.getCustomer().getDob());
		
		if (age >= 18) {
			return true;	
		}
		loan.setLoanStatus(LoanStatus.REJECTED);
		throw new LoanNotEligibleException("You Are not Eligible For Loan.");
	}
	
	private static int calculateAge(final LocalDate date) {
		return Period.between(date, LocalDate.now()).getYears();
	}

	public static final EducationLoanValidator getInstance() {
		if (instance == null) {
			instance = new EducationLoanValidator();
		}
		return instance;
	}
}
