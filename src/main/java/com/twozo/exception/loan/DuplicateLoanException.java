package com.twozo.exception.loan;

public class DuplicateLoanException extends Exception {
	private static final long serialVersionUID = -2084971938052976184L;

	public DuplicateLoanException(final String message) {
		super(message);
	}
}
