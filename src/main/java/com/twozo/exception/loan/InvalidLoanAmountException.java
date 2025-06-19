package com.twozo.exception.loan;

public class InvalidLoanAmountException extends Exception {
	private static final long serialVersionUID = 3078612063888015952L;
	
	public InvalidLoanAmountException(final String message) {
		super(message);
	}
}
