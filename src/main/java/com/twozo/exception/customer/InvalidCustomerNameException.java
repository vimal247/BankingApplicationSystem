package com.twozo.exception.customer;

public class InvalidCustomerNameException extends Exception {
	private static final long serialVersionUID = 6441957355902608611L;
	
	public InvalidCustomerNameException(final String message) {
		super(message);
	}
}
 