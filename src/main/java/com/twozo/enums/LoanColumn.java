package com.twozo.enums;

public enum LoanColumn {

	ACCOUNT_ID("account_id"),
	LOAN_NUMBER("loan_number"),
	LOAN_AMOUNT("loan_amount"),
	ACCOUNT_NUMBER("account_number"),
	LOAN_TYPE("loan_type"),
	LOAN_TENURE("loan_tenure"),
	EMI_AMOUNT("emi_amount"),
	BALANCE_AMOUNT("balance_amount"),
	LOAN_STATUS("loan_status"),
	APPLICATION_DATE("application_date"),
	INTEREST_RATE("interest_rate");
	
	private final String description; 

	LoanColumn(final String description) {
		this.description = description;
	}

	public final String getDescription() {
		return description;
	}
}
