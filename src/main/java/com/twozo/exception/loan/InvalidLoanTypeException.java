package com.twozo.exception.loan;

public class InvalidLoanTypeException extends Exception {
	private static final long serialVersionUID = 6616051043818537071L;
	
	public InvalidLoanTypeException(final String message) {
		super(message);
	}
}
