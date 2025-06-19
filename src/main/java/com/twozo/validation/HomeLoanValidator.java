package com.twozo.validation;

import com.twozo.enums.LoanStatus;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.model.HomeLoan;
import com.twozo.model.Loan;

public class HomeLoanValidator implements LoanValidator<HomeLoan> {
	private static HomeLoanValidator instance;
	
	@Override
	public boolean validate(HomeLoan homeLoan) throws LoanNotEligibleException {
		return checkEligibleForLoan(homeLoan, homeLoan.getIncome()); 
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
	
	public static final HomeLoanValidator getInstance() {
		if (instance == null) {
			instance = new HomeLoanValidator();
		}
		return instance;
	}
}