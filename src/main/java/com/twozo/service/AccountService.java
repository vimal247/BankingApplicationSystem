package com.twozo.service;

import com.twozo.enums.AccountType;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.model.Account;

public interface AccountService {
	boolean saveAccount(final Account account) throws AccountProcessingException, CustomerProcessingException;

	Account getAccountDetails(final String accountNumber) throws AccountProcessingException;

	boolean updateAccountType(final String accountNumber, final AccountType accountType) throws AccountProcessingException;

	boolean deactivateAccount(final String accountNumber) throws AccountProcessingException;

	boolean isAccountPresent(final String accountNumber) throws AccountProcessingException;
}
