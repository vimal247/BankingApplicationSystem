package com.twozo.exception.account;

public class AccountNotActiveException extends Exception {
	private static final long serialVersionUID = 697156679896825669L;

	public AccountNotActiveException(final String message) {
		super(message);
	}
}
