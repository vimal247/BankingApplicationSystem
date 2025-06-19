package com.twozo.exception.account;

public class InsufficientAmount extends Exception {
	private static final long serialVersionUID = -9032958189282064399L;
	
	public InsufficientAmount(final String message) {
		super(message);
	}
}
