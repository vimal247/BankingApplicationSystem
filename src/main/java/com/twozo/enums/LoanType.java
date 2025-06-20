package com.twozo.enums;

public enum LoanType {

	HOME_LOAN("homeloan"),
	PERSONAL_LOAN("personalloan"),
	EDUCATION_LOAN("educationloan");
	
	private final String description;

	LoanType(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
