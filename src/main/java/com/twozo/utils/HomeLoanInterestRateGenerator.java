package com.twozo.utils;

import com.twozo.exception.loan.InvalidLoanAmountException;
import com.twozo.model.HomeLoan;

public class HomeLoanInterestRateGenerator {

    private static final HomeLoanInterestRateGenerator INSTANCE = new HomeLoanInterestRateGenerator();

    public  static HomeLoanInterestRateGenerator getInstance() {
        return INSTANCE;
    }

    public double getInterestRate(final double loanAmount) throws InvalidLoanAmountException {
        if (loanAmount <= 50000) {
            return 2;
        } else if (loanAmount <= 100000) {
            return 3;
        } else if (loanAmount <= 500000) {
            return 5;
        } else if (loanAmount <= 1000000) {
            return 7;
        } else {
            throw new InvalidLoanAmountException("Invalid loan amount for Home Loan.");
        }
    }
}
