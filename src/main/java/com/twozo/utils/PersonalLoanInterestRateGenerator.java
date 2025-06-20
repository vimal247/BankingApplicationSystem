package com.twozo.utils;

import com.twozo.exception.loan.InvalidLoanAmountException;

public class PersonalLoanInterestRateGenerator {

    private static final PersonalLoanInterestRateGenerator INSTANCE = new PersonalLoanInterestRateGenerator();

    public static PersonalLoanInterestRateGenerator getINSTANCE() {
        return INSTANCE;
    }

    public double getInterestRate(final double loanAmount) throws InvalidLoanAmountException {
        if (loanAmount <= 50000) {
            return 1;
        } else if (loanAmount <= 100000) {
            return 2.5;
        } else if (loanAmount <= 500000) {
            return 3.5;
        } else if (loanAmount <= 1000000) {
            return 5;
        } else {
            throw new InvalidLoanAmountException("Invalid loan amount for Home Loan.");
        }
    }
}
