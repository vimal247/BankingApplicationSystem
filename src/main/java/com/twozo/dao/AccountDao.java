package com.twozo.dao;

import com.twozo.enums.AccountType;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;

public interface AccountDao { 
	
	/* ----- Account Services ----- */
	long saveAccount(final long customerId, final Account account) throws DuplicateEntryException, DatabaseException;

	Account getAccountByNumber(final String accountNumber) throws DatabaseException;

	boolean updateAccountDetail(final String accountNumber, final AccountType accountType) throws DatabaseException;

	boolean deactivateAccount(final String accountNumber) throws DatabaseException;
	
	boolean isUniqueMobileForAccount(final String mobileNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException;

	boolean isUniquePanCardNumber(final String panCardNumber, final AccountType accountType) throws DuplicateEntryException, DatabaseException;

	boolean isUniqueAadharForAccount(final String aadharNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException;
}
