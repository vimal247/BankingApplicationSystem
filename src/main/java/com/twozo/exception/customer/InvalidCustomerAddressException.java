package com.twozo.exception.customer;

public class InvalidCustomerAddressException extends Exception {
	private static final long serialVersionUID = 3931220212465710471L;

	public InvalidCustomerAddressException(final String message) {
		super(message);
	}

}
