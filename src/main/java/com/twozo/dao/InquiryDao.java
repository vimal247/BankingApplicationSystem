package com.twozo.dao;

import com.twozo.exception.database.DatabaseException;

public interface InquiryDao {
	/* ----- Inquiry Services ----- */
	double getBalance(final String accountNumber) throws DatabaseException;
}
