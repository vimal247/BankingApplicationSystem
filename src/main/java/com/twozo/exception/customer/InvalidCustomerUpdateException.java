package com.twozo.exception.customer;

public class InvalidCustomerUpdateException extends Exception {
	private static final long serialVersionUID = -4794220015082920707L;

	public InvalidCustomerUpdateException(final String message) {
		super(message);
	}
}
