package com.twozo.exception.account;

public class AccountNotFoundException extends Exception {
	private static final long serialVersionUID = -842292981349756306L;

	public AccountNotFoundException(final String message) {
		super(message);
	}
}
