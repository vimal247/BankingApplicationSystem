package com.twozo.service.impl;

import com.twozo.enums.BranchCode;
import com.twozo.enums.BranchName;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.account.InvalidBranchException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.model.Account;
import com.twozo.service.BankService;
import com.twozo.validation.AccountValidator;

public final class TirunelveliBankService extends BankService {

	private static BankService instance;

	/* Override The BankService Abstract Method. */
	@Override
	public final boolean createAccount(final Account account) throws AccountProcessingException, CustomerProcessingException {
		account.setBranchName(BranchName.TIRUNELVELI);
		account.setBranchCode(BranchCode.TIRUNELVELI);

		return ACCOUNT_SERVICE.saveAccount(account);
	}

	@Override
	public final boolean isAccountAvailableInBranch(final String accountNumber) throws AccountProcessingException {
		try {
			final Account account = ACCOUNT_SERVICE.getAccountDetails(accountNumber);

			if (account != null) {
				if (AccountValidator.validateBranchTirunelveli(account.getBranchCode().getCode())) {

					return ACCOUNT_SERVICE.isAccountPresent(accountNumber);
				}
				throw new InvalidBranchException("This Not Your Branch,Go & Visit Tenkasi Branch.");
			}
			throw new AccountNotFoundException("Account not found.");
		} catch (AccountProcessingException | AccountNotFoundException | InvalidBranchException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}
	}
	/* End Of The Override The BankService Abstract Method. */

	// Singleton Method For TenkasiBankService.
	public static final BankService getInstance() {
		if (instance == null) {
			instance = new TirunelveliBankService();
		}
		return instance;
	}

	// Private Constructor For TirunelveliBankService.
	private TirunelveliBankService() {

	}
}
