package com.twozo.dao.impl.collection;

import com.twozo.dao.AccountDao;
import com.twozo.dao.InquiryDao;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InquiryDaoCollectionImpl implements InquiryDao {

	private final AccountDao accountDao;

	@Autowired
	public InquiryDaoCollectionImpl(final AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	@Override
	public final double getBalance(final String accountNumber) throws AccountNotFoundException, DatabaseException {
		final Account account = accountDao.getAccountByNumber(accountNumber);
		return account.getBalance();
	}
}