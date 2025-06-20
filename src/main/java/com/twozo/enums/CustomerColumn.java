package com.twozo.enums;

public enum CustomerColumn {

	ID("id"),
	NAME("name"),
	GENDER("gender"),
	MOBILE_NUMBER("mobile_number"),
	AADHAR_NUMBER("aadhar_number"),
	PAN_CARD_NUMBER("pan_card_number"),
	DOB("dob"),
	ADDRESS("address"),
	PHOTO_URL("photo_url"); 
	
	private final String description;

	CustomerColumn(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
