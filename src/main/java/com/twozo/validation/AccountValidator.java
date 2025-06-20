package com.twozo.validation;

import java.util.Arrays;
import java.util.List;

import com.twozo.enums.AccountType;
import com.twozo.enums.BranchCode;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.account.MinimumAmountException;
import com.twozo.model.Account;

public final class AccountValidator {

	private static final double MIN_BALANCE = 500;

	// Private constructor to restrict instantiation outside the class, enforcing
	// Singleton pattern.
	private AccountValidator() {

	}

	// Validates the customer Account.
	public final static boolean validationAccount(final Account account) throws AccountProcessingException {
		final double amount = account.getBalance();
		final AccountType accountType = account.getAccountType();

		try {
			return validateAmount(amount) && validateAccountType(accountType);
		} catch (MinimumAmountException e) {
			throw new AccountProcessingException("Account error : " + e.getMessage(), e);
		}
	}

	// Validates the customer amount.
	private static boolean validateAmount(final Double amount) throws MinimumAmountException {
		if (amount != null && amount > MIN_BALANCE) {
			return true;
		} else {
			throw new MinimumAmountException("Initial Amount Is 500$.");
		}
	}

	// Validates the customer account type.
	private static boolean validateAccountType(final AccountType accountType) {
		if (accountType == null) {
			return false;
		}
		final List<String> validateTypesList = Arrays.asList("savings", "current", "fixed deposit",
				"recurring deposit");

		return validateTypesList.contains(accountType.name().toLowerCase().trim());
	}

	// Validates the customer branch (tenkasi).
	public final static boolean validateBranchTenkasi(final long branchCode) {

		return branchCode == BranchCode.TENKASI.getCode(); 
	}

	// Validates the customer branch (tirunelveli).
	public final static boolean validateBranchTirunelveli(final long branchCode) {

		return branchCode == BranchCode.TIRUNELVELI.getCode();
	}
}
