package com.twozo.enums;

public enum LoanStatus {

	PENDING, // Loan application is submitted but not yet processed
	APPROVED, // Loan is approved
	REJECTED, // Loan is rejected
	DISBURSED, // Loan amount has been disbursed to the borrower
	ACTIVE, // Loan is active and being repaid
	CLOSED, // Loan is fully repaid and closed
	DEFAULTED; // Loan is in default due to non-payment
}
