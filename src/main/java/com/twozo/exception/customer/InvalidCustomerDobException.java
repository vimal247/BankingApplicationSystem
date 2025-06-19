package com.twozo.exception.customer;

public class InvalidCustomerDobException extends Exception {
	private static final long serialVersionUID = 7296243898760657066L;

	public InvalidCustomerDobException(final String message) {
		super(message);
	}

}
