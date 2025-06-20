package com.twozo.dao;

import java.text.ParseException;

import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.customer.*;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Customer;

public interface CustomerDao {
	/* ----- Customer Services ----- */
	long saveCustomer(final Customer customer) throws DatabaseException;

	boolean updateCustomerDetails(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerNameException, DuplicateEntryException, InvalidCustomerAddressException,
			InvalidCustomerDobException, InvalidMobileNoException, ParseException, InvalidCustomerUpdateException,
			DuplicateUpdateException, DatabaseException, AccountNotFoundException;

	Customer getCustomerByAccountNumber(final String accountNumber) throws CustomerNotFoundException, DatabaseException, AccountNotFoundException;

	Customer getCustomerById(final long customerId) throws CustomerNotFoundException;
}
