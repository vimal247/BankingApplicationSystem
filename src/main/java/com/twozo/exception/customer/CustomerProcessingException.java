package com.twozo.exception.customer;

public class CustomerProcessingException extends Exception {

	private static final long serialVersionUID = -5968706276189041822L;

	public CustomerProcessingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
