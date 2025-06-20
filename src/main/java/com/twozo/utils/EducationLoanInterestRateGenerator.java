package com.twozo.utils;

import com.twozo.exception.loan.InvalidLoanAmountException;
import com.twozo.model.EducationLoan;

public class EducationLoanInterestRateGenerator {

    private static final EducationLoanInterestRateGenerator INSTANCE = new EducationLoanInterestRateGenerator();

    public static EducationLoanInterestRateGenerator getInstance() {
        return INSTANCE;
    }

    public double getInterestRate(final double loanAmount) throws InvalidLoanAmountException {
        if (loanAmount <= 50000) {
            return 0.5;
        } else if (loanAmount <= 100000) {
            return 1;
        } else if (loanAmount <= 500000) {
            return 1.5;
        } else if (loanAmount <= 1000000) {
            return 2;
        } else {
            throw new InvalidLoanAmountException("Invalid loan amount for Education Loan");
        }
    }
}
