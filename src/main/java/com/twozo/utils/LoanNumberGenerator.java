package com.twozo.utils;

import com.twozo.enums.LoanType;

public class LoanNumberGenerator {

    private int uniqueNumber = 1001;

    private static final LoanNumberGenerator INSTANCE = new LoanNumberGenerator();

    public static LoanNumberGenerator getInstance() {
        return  INSTANCE;
    }

    public String generateLoanNumber(final LoanType loanType) {

        if (loanType != null) {
            switch (loanType.name().toLowerCase()) {
                case "homeloan":
                    return generateHomeLoanNumber();
                case "personalloan":
                    return generateEducationLoanNumber();
                case "educationloan":
                    return generatePersonalLoanNumber();
            }
        }
        return "";
    }

    private String generateHomeLoanNumber() {
        return "HLOAN" + getUniqueNumber();
    }

    private String generateEducationLoanNumber() {
        return "ELOAN" + getUniqueNumber();
    }

    private String generatePersonalLoanNumber() {
        return "PLOAN" + getUniqueNumber();
    }

    private int getUniqueNumber() {
        return uniqueNumber++;
    }
}
