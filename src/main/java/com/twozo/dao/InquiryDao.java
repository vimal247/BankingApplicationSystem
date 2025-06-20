package com.twozo.dao;

import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.inquiry.NoBalanceFoundException;

public interface InquiryDao {
	/* ----- Inquiry Services ----- */
	double getBalance(final String accountNumber) throws NoBalanceFoundException, DatabaseException, AccountNotFoundException;
}
