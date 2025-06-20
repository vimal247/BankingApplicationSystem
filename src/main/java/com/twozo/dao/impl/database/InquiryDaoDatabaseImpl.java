package com.twozo.dao.impl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.twozo.dao.InquiryDao;
import com.twozo.exception.database.DatabaseException;
import com.twozo.database.DatabaseConnection;
import com.twozo.exception.inquiry.NoBalanceFoundException;
import com.twozo.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class InquiryDaoDatabaseImpl implements InquiryDao {

	private InquiryRepository inquiryRepository;

	@Autowired
	public InquiryDaoDatabaseImpl(final InquiryRepository inquiryRepository){
		this.inquiryRepository = inquiryRepository;
	}
	/* ----- Inquiry Services ----- */
	@Override
	public final double getBalance(final String accountNumber) throws NoBalanceFoundException {
		final Double balance = inquiryRepository.findBalanceByAccountNumber(accountNumber);

		if (balance == null) {
			throw new NoBalanceFoundException("No balance found for account number: " + accountNumber);
		}
		return balance;
	}

	private  InquiryDaoDatabaseImpl() {

	}
}
