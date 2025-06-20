package com.twozo.exception.account;

public class InvalidAccountTypeException extends Exception {
	private static final long serialVersionUID = 3519787430768538538L;
	
	public InvalidAccountTypeException(final String message ) {
		super(message);
	}
}
