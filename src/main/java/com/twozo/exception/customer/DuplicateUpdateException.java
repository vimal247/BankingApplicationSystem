package com.twozo.exception.customer;

public class DuplicateUpdateException extends Exception {
	private static final long serialVersionUID = 269455123796341658L;

	public DuplicateUpdateException(final String message) {
		super(message);
	}
}
