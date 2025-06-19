package com.twozo.exception.customer;

public class DuplicateEntryException extends Exception {
	private static final long serialVersionUID = 8238186406189749570L;
	
	public DuplicateEntryException(final String message) {
		super(message);
	}
	
}
