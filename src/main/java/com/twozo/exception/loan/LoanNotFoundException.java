package com.twozo.exception.loan;

public class LoanNotFoundException extends Exception {
	private static final long serialVersionUID = -8556456941831798292L;
	
	public LoanNotFoundException(String message) {
		super(message);
	}
	
}
