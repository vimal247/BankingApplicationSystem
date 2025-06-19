package com.twozo.exception.loan;

public class LoanProcessingException extends Exception {

	private static final long serialVersionUID = 6155165075272559179L;

	public LoanProcessingException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
