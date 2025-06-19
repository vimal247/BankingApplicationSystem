package com.twozo.validation;

import com.twozo.exception.account.MinimumAmountException;
import com.twozo.model.Account;

public class TransactionValidator {
	private static TransactionValidator instance;
	private final double MIN_BALANCE = 500;
	private final double MIN_WITHDRAW_AMOUNT = 100;

	private TransactionValidator() {

	}

	public final static TransactionValidator getInstance() {
		if (instance == null) {
			instance = new TransactionValidator();
		}
		return instance;
	}

	public final boolean validateWithdrawAmount(final Account account, final double amount)
			throws MinimumAmountException {
		if (amount < 0) {
			throw new MinimumAmountException("Please Enter a Valid Amount.");
		}
		if (!(amount > MIN_WITHDRAW_AMOUNT)) {
			throw new MinimumAmountException("Minimun Withdraw Amount Is 100 Above.");
		} else if (!(MIN_BALANCE <= account.getBalance() - amount)) {
			throw new MinimumAmountException(" Your Minimun Balance is Low.");
		}
		return true;
	}

	public final boolean validateDepositAmount(final Account account, final double amount)
			throws MinimumAmountException {
		if (amount < 0) {
			throw new MinimumAmountException("Please Enter a Valid Amount.");
		}
		return true;
	}

}
