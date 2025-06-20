package com.twozo.enums;

public enum HomeLoanColumn {

	LOAN_ID("loan_id"),
	INCOME("income"),
	EMPLOYMENT_TYPE("employment_type"),
	PROPERTY_ADDRESS("property_address"),
	PROPERTY_TYPE("property_type");
	
	private final String description; 

	HomeLoanColumn(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
