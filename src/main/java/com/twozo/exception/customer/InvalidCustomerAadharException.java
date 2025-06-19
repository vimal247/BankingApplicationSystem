package com.twozo.exception.customer;

public class InvalidCustomerAadharException extends Exception {
	private static final long serialVersionUID = 6902572906017557775L;

	public InvalidCustomerAadharException(final String message) {
		super(message);
	}

}
