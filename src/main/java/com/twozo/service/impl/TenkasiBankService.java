package com.twozo.service.impl;

import com.twozo.enums.BranchCode;
import com.twozo.enums.BranchName;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.account.InvalidBranchException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.model.Account;
import com.twozo.service.AccountService;
import com.twozo.service.BankService;
import com.twozo.validation.AccountValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class TenkasiBankService extends BankService {

	private final AccountService accountService;

	@Autowired
	public  TenkasiBankService(final AccountService accountService){
        this.accountService = accountService;
	}

	/* Override The BankService Abstract Method. */
	@Override
	public final boolean createAccount(final Account account) throws AccountProcessingException, CustomerProcessingException {
		account.setBranchName(BranchName.TENKASI);
		account.setBranchCode(BranchCode.TENKASI);
		
		return accountService.saveAccount(account);
	}

	@Override
	public final boolean isAccountPresentInBranch(final String accountNumber) throws AccountProcessingException{
		try {
			final Account account = accountService.getAccountDetails(accountNumber);
 
			if (account != null) {
				if (AccountValidator.validateBranchTenkasi(account.getBranchCode().getCode())) {

					return accountService.isAccountAvailable(accountNumber);
				}
				throw new InvalidBranchException("This Not Your Branch,Go & Visit Tenkasi Branch.");
			}
			throw new AccountNotFoundException("Account not found.");
		} catch (AccountProcessingException | AccountNotFoundException | InvalidBranchException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}
	}
	/* End Of The Override The BankService Abstract Method. */
}
