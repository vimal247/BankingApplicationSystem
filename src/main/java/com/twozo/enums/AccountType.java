package com.twozo.enums;

public enum AccountType {

	SAVINGS("savings"),
	CURRENT("current"),
	FIXED_DEPOSIT("fixed deposit"),
	RECURRING_DEPOSIT("recurring deposit");
	
	private final String description;

	AccountType(final String description) {
		this.description = description;
	}
 
	public final String getDescription() {
		return description;
	}
}
