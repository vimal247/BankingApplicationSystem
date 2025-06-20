package com.twozo.exception.transaction;

public class TransactionProcessingException extends Exception {
	
	private static final long serialVersionUID = -885796398294028448L;
	
	public TransactionProcessingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
