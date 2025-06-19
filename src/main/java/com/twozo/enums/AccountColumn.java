package com.twozo.enums;

public enum AccountColumn {

	ID("id"),
	CUSTOMER_ID("customer_id"),
	ACCOUNT_NUMBER("account_number"),
	IFSC_CODE("ifsc_code"),
	BRANCH_NAME("branch_name"),
	ACCOUNT_TYPE("account_type"),
	BALANCE("balance"),
	ACCOUNT_STATUS("account_status"),
	BRANCH_CODE("branch_code");
	
	private final String description; 

	AccountColumn(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
