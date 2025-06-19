package com.twozo.dao;

import java.text.ParseException;

import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerAddressException;
import com.twozo.exception.customer.InvalidCustomerDobException;
import com.twozo.exception.customer.InvalidCustomerNameException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.customer.InvalidMobileNoException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Customer;

public interface CustomerDao {
	/* ----- Customer Services ----- */
	long saveCustomer(final Customer customer) throws DatabaseException;

	boolean updateCustomerDetails(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerNameException, DuplicateEntryException, InvalidCustomerAddressException,
			InvalidCustomerDobException, InvalidMobileNoException, ParseException, InvalidCustomerUpdateException,
			DuplicateUpdateException, DatabaseException;

	Customer getCustomerByAccountNumber(final String accountNumber) throws DatabaseException;

	Customer getCustomerById(final long customerId) throws DatabaseException;	
}
