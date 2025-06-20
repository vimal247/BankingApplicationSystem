package com.twozo.exception.account;

public class AccountCreationFailedException extends Exception {
	private static final long serialVersionUID = 5889873597156708369L;
	
	public AccountCreationFailedException(final String message) {
		super(message);
	}
}
