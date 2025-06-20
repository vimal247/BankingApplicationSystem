package com.twozo.exception.loan;

public class LoanNotEligibleException extends Exception {
	private static final long serialVersionUID = 3249801137751066446L;
	
	public LoanNotEligibleException(String message) {
		super(message);
	}
	
}
