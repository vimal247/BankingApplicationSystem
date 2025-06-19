package com.twozo.exception.database;

public class DatabaseException extends Exception {
	private static final long serialVersionUID = -5581067321598887411L;

	public DatabaseException(final String message, final Throwable cause) {
		super(message, cause);	
	} 		 
}