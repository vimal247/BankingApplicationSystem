package com.twozo.dao.impl.database;

import com.twozo.dao.AccountDao;
import com.twozo.enums.AccountStatus;
import com.twozo.enums.AccountType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.AccountNumberExistException;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AccountDaoDatabaseImpl implements AccountDao {

	private AccountRepository accountRepository;

	@Autowired
	public AccountDaoDatabaseImpl(final AccountRepository accountRepository){
		this.accountRepository = accountRepository;
	}

	/* ----- Account Services ----- */
	@Override
	public long saveAccount(final long customerId, final Account account) throws AccountNumberExistException {
		if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
			throw new AccountNumberExistException("Account number already exists.");
		}

		final Account savedAccount = accountRepository.save(account);
		return savedAccount.getId();
    }

	@Override
	public final Account getAccountByNumber(final String accountNumber) throws DatabaseException, AccountNotFoundException {
		final Account account = accountRepository.findByAccountNumber(accountNumber);

		if (account == null) {
			throw new AccountNotFoundException("Account not found");
		}
		return account;
	}

	@Override
	public boolean updateAccountDetail(final String accountNumber, final AccountType accountType)
            throws DatabaseException, AccountNotFoundException {
		final Account account = getAccountByNumber(accountNumber);

		account.setAccountType(accountType);
		accountRepository.save(account);

		return true;
	}

	@Override
	public boolean deactivateAccount(final String accountNumber) throws DatabaseException, AccountNotFoundException {
		final Account account = getAccountByNumber(accountNumber);

		account.setAccountStatus(AccountStatus.INACTIVE);
		accountRepository.save(account);
		return true;
	}

	@Override
	public final boolean isUniquePanCardNumber(final String panCardNumber, final AccountType accountType)
			throws DuplicateEntryException {

		return isUniqueForAccountType("pan_card_number", panCardNumber, accountType);
	}

	@Override
	public boolean isUniqueAadharForAccount(final String aadharNumber, final AccountType accountType)
			throws DuplicateEntryException {

		return isUniqueForAccountType("aadhar_number", aadharNumber, accountType);
	}

	@Override
	public boolean isUniqueMobileForAccount(final String mobileNumber, final AccountType accountType)
			throws DuplicateEntryException {

		return isUniqueForAccountType("mobile_number", mobileNumber, accountType);
	}

	private boolean isUniqueForAccountType(final String columnName, final String value, final AccountType accountType)
			throws DuplicateEntryException {
		if (accountRepository.existsByColumnAndAccountType(columnName, value, accountType)) {
			throw new DuplicateEntryException("Entry already exists for account type: " + accountType);
		}
		return true;
	}

	private AccountDaoDatabaseImpl() {

	}
}
