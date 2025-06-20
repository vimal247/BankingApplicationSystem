package com.twozo.enums;

public enum EmploymentType {

	SELF_EMPLOYED("self employed"), 
	SALARIED("salaried"),
	BUSINESS_OWNER("business owner"),
	RETIRED("retired"),
	UNEMPLOYED("unemployed"),
	FREELANCER("freelancer");
 
	private final String description;

	EmploymentType(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
