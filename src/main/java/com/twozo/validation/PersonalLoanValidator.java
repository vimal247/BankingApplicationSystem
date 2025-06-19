package com.twozo.validation;

import com.twozo.enums.LoanStatus;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;

public class PersonalLoanValidator implements LoanValidator<PersonalLoan>{
	private static PersonalLoanValidator instance;
	
	@Override
	public boolean validate(final PersonalLoan personalLoan) throws LoanNotEligibleException {
		
		return checkEligibleForLoan(personalLoan, personalLoan.getIncome());
	}
	
	private static boolean checkEligibleForLoan(final Loan loan, final double income) throws LoanNotEligibleException {
		final double loanAmount = loan.getLoanAmount() * (1 + (loan.getInterestRate() / 100));
		final int months = loan.getLoanTenure().getYears() * 12;
		final double personPayableMoney = income / 2;
		final double monthlyPayableMoney = loanAmount / months;
		loan.setEmiAmount(monthlyPayableMoney);
		
		if (personPayableMoney < monthlyPayableMoney) {
			loan.setLoanStatus(LoanStatus.REJECTED);
			throw new LoanNotEligibleException("You Are not Eligible For Loan.");
		}
		return true;
	}

	public static final PersonalLoanValidator getInstance() {
		if (instance == null) {
			instance = new PersonalLoanValidator();
		}
		return instance;
	}
}
