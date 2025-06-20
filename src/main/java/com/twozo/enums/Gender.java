package com.twozo.enums;

public enum Gender {

	MALE("male"),
	FEMALE("female"),
	OTHERS("others");
	
	private final String description;

	Gender(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
