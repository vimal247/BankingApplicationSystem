package com.twozo.dao.impl.collection;

import com.twozo.dao.AccountDao;
import com.twozo.dao.InquiryDao;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;

public class InquiryDaoCollectionImpl implements InquiryDao {
	private static final AccountDao ACCOUNT_DAO = AccountDaoCollectionImpl.getInstance();
	
	@Override
	public final double getBalance(final String accountNumber) throws DatabaseException {
		final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);
		return account.getBalance();
	}
}
