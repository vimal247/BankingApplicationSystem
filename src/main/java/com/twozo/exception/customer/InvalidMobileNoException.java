package com.twozo.exception.customer;

public class InvalidMobileNoException extends Exception {
	private static final long serialVersionUID = 2785029550080596646L;

	public InvalidMobileNoException(final String message) {
		super(message);
	}
}
