package com.twozo.exception.account;

public class AccountProcessingException extends Exception {

	private static final long serialVersionUID = -6081584557762024991L;

	public AccountProcessingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
