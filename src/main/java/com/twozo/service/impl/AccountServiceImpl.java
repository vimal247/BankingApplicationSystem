package com.twozo.service.impl;

import java.time.LocalDateTime;

import com.twozo.dao.AccountDao;
import com.twozo.dao.TransactionDao;
import com.twozo.dao.impl.database.AccountDaoDatabaseImpl;
import com.twozo.dao.impl.database.TransactionDaoDatabaseImpl;
import com.twozo.enums.AccountStatus;
import com.twozo.enums.AccountType;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountCreationFailedException;
import com.twozo.exception.account.AccountNotActiveException;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.account.InvalidAccountTypeException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.service.AccountService;
import com.twozo.service.CustomerService;

public final class AccountServiceImpl implements AccountService {

	private static int uniqueNumber = 1001;
	private static AccountServiceImpl instance;

	private static final AccountDao ACCOUNT_DAO = AccountDaoDatabaseImpl.getInstance();
	private static final CustomerService CUSTOMER_SERVICE = CustomerServiceImpl.getInstance();
	private static final TransactionDao TRANSACTION_DAO = TransactionDaoDatabaseImpl.getInstance();

	// Private constructor to restrict instantiation outside the class, enforcing
	// Singleton pattern.
	private AccountServiceImpl() {

	}

	// Save account method for Storage, including customer details.
	@Override
	public final boolean saveAccount(final Account account)
			throws AccountProcessingException, CustomerProcessingException {
		try {
			final AccountType accountType = account.getAccountType();
			final String aadharNumber = account.getCustomer().getAadharNumber();
			final String accountNumber = generateAccountNumber(accountType, aadharNumber);
			final String ifscCode = generateIfscCode(account);

			account.setAccountNumber(accountNumber);
			account.setIfscCode(ifscCode);
			account.setAccountStatus(AccountStatus.ACTIVE);

			final long customerId = CUSTOMER_SERVICE.saveCustomerFromAccount(account);

			if (customerId != 0) {
				final long transactionId = ACCOUNT_DAO.saveAccount(customerId, account); 
				
				if (transactionId != 0) {
					return TRANSACTION_DAO.saveAccountTransaction(transactionId, account, account.getBalance(),
							LocalDateTime.now(), TransactionType.DEPOSIT, 0);
				}
				throw new AccountCreationFailedException("Account creation failed.");
			}
		} catch (AccountCreationFailedException | InvalidAccountTypeException | DuplicateEntryException
				| CustomerProcessingException | DatabaseException e) {
			throw new AccountProcessingException(e.getMessage(), e);
        }
		return false;
	}

	// Retrieve account data from Storage.
	@Override
	public final Account getAccountDetails(final String accountNumber) throws AccountProcessingException {
		try {
			Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);

			if (account == null) {
				throw new AccountNotFoundException("Account not found.");
			}
			return account;
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}
	}

	// Update the account type in the storage (database).
	@Override
	public final boolean updateAccountType(final String accountNumber, final AccountType accountType)
			throws AccountProcessingException {
		try {
			return ACCOUNT_DAO.updateAccountDetail(accountNumber, accountType);
		} catch (DatabaseException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}
	}

	// Deactivate the account using Storage.
	@Override
	public final boolean deactivateAccount(final String accountNumber) throws AccountProcessingException {
		try {
			return ACCOUNT_DAO.deactivateAccount(accountNumber);
		} catch (DatabaseException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}

	}

	// Check if account number is Present & Active in Storage.
	@Override
	public final boolean isAccountPresent(final String accountNumber) throws AccountProcessingException {
		try {
			final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);

			if (account != null) {
				if (account.getAccountStatus().equals(AccountStatus.ACTIVE)) {
					return true;
				}
				throw new AccountNotActiveException("Account is Not Active.");
			}
			throw new AccountNotFoundException("Account Not Founded.");
		} catch (DatabaseException | AccountNotFoundException | AccountNotActiveException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}
	}

	// Singleton method for CustomerServiceImpl, ensuring only one instance of the
	// class.
	public final static AccountServiceImpl getInstance() {
		if (instance == null) {
			instance = new AccountServiceImpl();
		}
		return instance;
	}

	/* Generation part for Account number & IFSC Code. */
	private String generateIfscCode(final Account account) {
		final long customerData = account.getBranchCode().getCode();

		if (customerData != 0) {
			return "IOB" + account.getBranchCode() + generateUniqueNumber();
		}
		return null;
	}

	private int generateUniqueNumber() {
		uniqueNumber++;
		return uniqueNumber;
	}

	private String generateAccountNumber(final AccountType accountType, final String aadharNumber)
			throws InvalidAccountTypeException {
		if (accountType != null) {
			switch (accountType.name().toLowerCase()) {
			case "savings":
				return generateSavingsAccountNumber(aadharNumber);
			case "current":
				return generateCurrentAccountNumber(aadharNumber);
			case "fixed deposit":
				return generateFixedDepositAccountNumber(aadharNumber);
			case "recurring deposit":
				return generateRecurringDepositAccountNumber(aadharNumber);
			}
		}
		throw new InvalidAccountTypeException("Invalid account type!");
	}

	private String generateSavingsAccountNumber(final String aadharNumber) {

		return "IOB-SAV-" + aadharNumber.hashCode();
	}

	private String generateCurrentAccountNumber(final String aadharNumber) {

		return "IOB-CUR-" + aadharNumber.hashCode();
	}

	private String generateFixedDepositAccountNumber(final String aadharNumber) {

		return "IOB-FD-" + aadharNumber.hashCode();
	}

	private String generateRecurringDepositAccountNumber(final String aadharNumber) {

		return "IOB-RD-" + aadharNumber.hashCode();
	}
	/*
	 * End of generation part.
	 */
}
