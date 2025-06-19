package com.twozo.dao.impl.collection;

import java.util.HashMap;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.enums.AccountStatus;
import com.twozo.enums.AccountType;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.model.Account;

public class AccountDaoCollectionImpl implements AccountDao {
	
	long transactionId = 1;
	private static AccountDaoCollectionImpl instance;
	
	private static final Map<String, Account> ACCOUNT_MAP = new HashMap<>();
	private static final Map<AccountType, Map<String, Account>> ACCOUNTS_BY_TYPE = new HashMap<>();
	
	@Override	
	public long saveAccount(final long customerId, final Account account) {
		if (account.getAccountNumber().isEmpty() || account == null) {
			return 0; 
		} 
		ACCOUNT_MAP.put(account.getAccountNumber(), account);
		ACCOUNTS_BY_TYPE.put(account.getAccountType(), ACCOUNT_MAP);
		 
		return transactionId++; 
	}

	@Override
	public final Account getAccountByNumber(final String accountNumber) {
		return ACCOUNT_MAP.get(accountNumber);
	}

	@Override 
	public final boolean deactivateAccount(final String accountNumber) {
		final Account account = ACCOUNT_MAP.get(accountNumber);
		
		if (account != null) {
			account.setAccountStatus(AccountStatus.INACTIVE);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean updateAccountDetail(final String accountNumber, final AccountType accountType) {
		final Account account = ACCOUNT_MAP.get(accountNumber);

		if (account != null) {
			account.setAccountType(accountType);
			return true;
		}
		return false;
	}
	
	@Override
	public final boolean isUniqueAadharForAccount(final String aadharNumber, final AccountType accountType)
			throws DuplicateEntryException {

		// Get accounts for the given account type
		Map<String, Account> accountsByType = ACCOUNTS_BY_TYPE.get(accountType); 

		if (accountsByType != null) {
			for (Account customerAccount : accountsByType.values()) {
				if (customerAccount.getCustomer().getAadharNumber().equalsIgnoreCase(aadharNumber)) {
					throw new DuplicateEntryException("Aadhar Number is Already Exist.");
				}
			}
		}
		return true;
	}

	@Override
	public final boolean isUniqueMobileForAccount(final String mobileNumber, final AccountType accountType)
			throws DuplicateEntryException {
		Map<String, Account> accountsByType = ACCOUNTS_BY_TYPE.get(accountType);

		if (accountsByType != null) {
			for (Account customerAccount : accountsByType.values()) {
				if (customerAccount.getCustomer().getMobileNumber().equalsIgnoreCase(mobileNumber)) {
					throw new DuplicateEntryException("Mobile Number is Already Exist.");
				}
			}
		}
		return true;
	}

	@Override
	public final boolean isUniquePanCardNumber(final String panCardNumber, final AccountType accountType)
			throws DuplicateEntryException {
		Map<String, Account> accountsByType = ACCOUNTS_BY_TYPE.get(accountType);

		if (accountsByType != null) {
			for (Account customerAccount : accountsByType.values()) {
				if (customerAccount.getCustomer().getPanCardNumber().equalsIgnoreCase(panCardNumber)) {
					throw new DuplicateEntryException("Aadhar Number is Already Exist.");
				}
			}
		}
		return true;
	}
	
	public static final AccountDaoCollectionImpl getInstance() {
		if (instance == null) {
			instance = new AccountDaoCollectionImpl();
		}
		return instance;
	}
}
