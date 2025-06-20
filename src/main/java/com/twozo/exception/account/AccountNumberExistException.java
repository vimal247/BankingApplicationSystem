package com.twozo.exception.account;

public class AccountNumberExistException extends Exception {

    public AccountNumberExistException(final String message) {
        super(message);
    }
}
