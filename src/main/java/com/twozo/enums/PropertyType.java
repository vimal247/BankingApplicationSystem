package com.twozo.enums;

public enum PropertyType {

	COMMERCIAL_PROPERTY("Commercial property"), 
	APARTMENT("apartment"), 
	VILLA("villa"),
	INDEPENDENT_HOUSE("independent house"), 
	LAND("land"), 
	FARM_HOUSE("farm house");

	private final String description;

	PropertyType(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
