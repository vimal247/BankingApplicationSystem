package com.twozo.enums;

public enum LoanAmount {

	FIFTY_THOUSAND(50_000), 
	ONE_LAKH(100_000),
	FIVE_LAKH(500_000), 
	TEN_LAKH(1_000_000);
	
	private final double amount;

    LoanAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}
