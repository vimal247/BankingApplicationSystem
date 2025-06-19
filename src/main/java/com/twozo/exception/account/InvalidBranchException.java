package com.twozo.exception.account;

public class InvalidBranchException extends Exception { 
	private static final long serialVersionUID = 1L;

	public InvalidBranchException(final String message) {
		super(message);
	}
}
