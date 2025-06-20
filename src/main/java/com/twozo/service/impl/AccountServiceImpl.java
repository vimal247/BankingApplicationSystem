package com.twozo.service.impl;

import java.time.LocalDateTime;

import com.twozo.dao.AccountDao;
import com.twozo.dao.TransactionDao;
import com.twozo.enums.AccountStatus;
import com.twozo.enums.AccountType;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.*;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.service.AccountService;
import com.twozo.service.CustomerService;
import com.twozo.utils.AccountNumberGenerator;
import com.twozo.utils.IfscCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class AccountServiceImpl implements AccountService {

	private final AccountDao accountDao;
	private final CustomerService customerService;
	private final TransactionDao transactionDao;

	@Autowired
	public AccountServiceImpl(final AccountDao accountDao, final CustomerService customerService, final TransactionDao transactionDao) {
		this.accountDao = accountDao;
		this.customerService = customerService;
		this.transactionDao = transactionDao;
	}

	// Save account method for Storage, including customer details.
	@Override
	public final boolean saveAccount(final Account account) throws AccountProcessingException {
		try {
			final AccountType accountType = account.getAccountType();
			final String aadharNumber = account.getCustomer().getAadharNumber();
			final String accountNumber = AccountNumberGenerator.getInstance().generateAccountNumber(accountType, aadharNumber);
			final String ifscCode = IfscCodeGenerator.getInstance().generateIfscCode(account);

			account.setAccountNumber(accountNumber);
			account.setIfscCode(ifscCode);
			account.setAccountStatus(AccountStatus.ACTIVE);

			final long customerId = customerService.saveCustomerFromAccount(account);

			if (customerId != 0) {
				final long transactionId = accountDao.saveAccount(customerId, account);
				
				if (transactionId != 0) {
					return transactionDao.saveAccountTransaction(transactionId, account, account.getBalance(),
							LocalDateTime.now(), TransactionType.DEPOSIT, 0);
				}
				throw new AccountCreationFailedException("Account creation failed.");
			}
		} catch (AccountCreationFailedException | InvalidAccountTypeException | CustomerProcessingException
				 | DatabaseException | AccountNumberExistException e) {
			throw new AccountProcessingException(e.getMessage(), e);
        }
		return false;
	}

	// Retrieve account data from Storage.
	@Override
	public final Account getAccountDetails(final String accountNumber) throws AccountProcessingException {
		try {
			Account account = accountDao.getAccountByNumber(accountNumber);

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
	public final boolean updateAccountType(final String accountNumber, final AccountType accountType) throws AccountProcessingException {
		try {
			return accountDao.updateAccountDetail(accountNumber, accountType);
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}
	}

	// Deactivate the account using Storage.
	@Override
	public final boolean deactivateAccount(final String accountNumber) throws AccountProcessingException {
		try {
			return accountDao.deactivateAccount(accountNumber);
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new AccountProcessingException(e.getMessage(), e);
		}

	}

	// Check if account number is Present & Active in Storage.
	@Override
	public final boolean isAccountAvailable(final String accountNumber) throws AccountProcessingException {
		try {
			final Account account = accountDao.getAccountByNumber(accountNumber);

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
}
