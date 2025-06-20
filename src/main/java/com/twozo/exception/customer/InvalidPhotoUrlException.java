package com.twozo.exception.customer;

public class InvalidPhotoUrlException extends Exception {
	private static final long serialVersionUID = 4714885404121838644L;

	public InvalidPhotoUrlException(final String message) {
		super(message);
	}
}
