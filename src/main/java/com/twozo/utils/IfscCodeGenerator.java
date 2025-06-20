package com.twozo.utils;

import com.twozo.model.Account;

public class IfscCodeGenerator {

    private int uniqueNumber = 1001;

    private static final IfscCodeGenerator INSTANCE = new IfscCodeGenerator();

    public static IfscCodeGenerator getInstance() {
        return INSTANCE;
    }

    /* Generation part for Account number & IFSC Code. */
    public String generateIfscCode(final Account account) {
        final long customerData = account.getBranchCode().getCode();

        if (customerData != 0) {
            return "IOB" + account.getBranchCode() + generateUniqueNumber();
        }
        return null;
    }

    private int generateUniqueNumber() {
        uniqueNumber++;
        return uniqueNumber;
    }
}
