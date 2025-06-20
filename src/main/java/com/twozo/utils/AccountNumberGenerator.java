package com.twozo.utils;

import com.twozo.enums.AccountType;
import com.twozo.exception.account.InvalidAccountTypeException;

public class AccountNumberGenerator {

    private static  final AccountNumberGenerator INSTANCE = new AccountNumberGenerator();

    public static AccountNumberGenerator getInstance() {
        return  INSTANCE;
    }

    public String generateAccountNumber(final AccountType accountType, final String aadharNumber)
            throws InvalidAccountTypeException {
        if (accountType != null) {
            switch (accountType.name().toLowerCase()) {
                case "savings":
                    return generateSavingsAccountNumber(aadharNumber);
                case "current":
                    return generateCurrentAccountNumber(aadharNumber);
                case "fixed deposit":
                    return generateFixedDepositAccountNumber(aadharNumber);
                case "recurring deposit":
                    return generateRecurringDepositAccountNumber(aadharNumber);
            }
        }
        throw new InvalidAccountTypeException("Invalid account type!");
    }

    private String generateSavingsAccountNumber(final String aadharNumber) {

        return "IOB-SAV-" + aadharNumber.hashCode();
    }

    private String generateCurrentAccountNumber(final String aadharNumber) {

        return "IOB-CUR-" + aadharNumber.hashCode();
    }

    private String generateFixedDepositAccountNumber(final String aadharNumber) {

        return "IOB-FD-" + aadharNumber.hashCode();
    }

    private String generateRecurringDepositAccountNumber(final String aadharNumber) {

        return "IOB-RD-" + aadharNumber.hashCode();
    }
}
