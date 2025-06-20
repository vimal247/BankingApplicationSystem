package com.twozo.enums;

public enum EducationalLoanColumn {

	EDUCATIONAL_LEVEL("educational_level"),
	INSTITUTION_NAME("institution_name"),
	COURSE_NAME("course_name"),
	COURSE_DURATION("course_duration");
	
	private final String description; 

	EducationalLoanColumn(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
