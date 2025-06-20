package com.twozo.exception.account;

public class MinimumAmountException extends Exception {
	private static final long serialVersionUID = -5275660210894465119L;
	
	public MinimumAmountException(final String message) {
		super(message);
	}
}
