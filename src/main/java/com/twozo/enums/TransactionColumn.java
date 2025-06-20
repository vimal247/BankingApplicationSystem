package com.twozo.enums;

public enum TransactionColumn {

	ID("id"),
	ACCOUNT_NUMBER("account_number"),
	TRANSFERED_ACCOUNT_NUMBER("transfered_account_number"),
	DATE_TIME("date_time"),
	AMOUNT("amount"),
	TRANSACTION_TYPE("transaction_type");
	
	private final String description; 

	TransactionColumn(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
