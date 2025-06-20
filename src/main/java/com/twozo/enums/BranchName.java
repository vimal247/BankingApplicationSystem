package com.twozo.enums;

public enum BranchName {

	TIRUNELVELI("tirunelveli"),
	TENKASI("tenkasi");
	
	private final String description;

	BranchName(final String description) {
		this.description = description;
	}
 
	public final String getDescription() {
		return description;
	}
}
