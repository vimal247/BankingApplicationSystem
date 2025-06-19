package com.twozo.exception.customer;

public class InvalidPanCardNumberException extends Exception {
	private static final long serialVersionUID = -9209669533558263205L;

	public InvalidPanCardNumberException(final String message) {
		super(message);
	}
}
