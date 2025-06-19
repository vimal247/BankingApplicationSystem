package com.twozo.service.impl;

import java.text.ParseException;

import com.twozo.dao.AccountDao;
import com.twozo.dao.CustomerDao;
import com.twozo.dao.impl.database.AccountDaoDatabaseImpl;
import com.twozo.dao.impl.database.CustomerDaoDatabaseImpl;
import com.twozo.enums.AccountType;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerAddressException;
import com.twozo.exception.customer.InvalidCustomerDobException;
import com.twozo.exception.customer.InvalidCustomerNameException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.customer.InvalidMobileNoException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.service.CustomerService;

public final class CustomerServiceImpl implements CustomerService {

	private static CustomerService instance;

	private static final CustomerDao CUSTOMER_DAO = CustomerDaoDatabaseImpl.getInstance();
	private static final AccountDao ACCOUNT_DAO = AccountDaoDatabaseImpl.getInstance();

	// Save customer data in storage after performing uniqueness checks for mobile
	// number and Aadhar number.
	@Override
	public final long saveCustomerFromAccount(final Account account) throws CustomerProcessingException {
		final Customer customer = account.getCustomer();
		final String aadharNumber = customer.getAadharNumber();
		final String mobileNumber = customer.getMobileNumber();
		final AccountType accountType = account.getAccountType();
		final String panCardNumber = customer.getPanCardNumber();

		try {
			if (!(isUniqueAadhar(aadharNumber, accountType) && isUniqueMobile(mobileNumber, accountType)
					&& isUniquePanCardNumber(panCardNumber, accountType))) {
				return 0;
			}

			return CUSTOMER_DAO.saveCustomer(customer);

		} catch (DatabaseException | DuplicateEntryException e) {
			throw new CustomerProcessingException(e.getMessage(), e);
		}
	}

	// Retrieve customer data from Storage.
	@Override
	public final Customer getCustomerByAccountNumber(final String accountNumber) throws CustomerProcessingException {
		try {
			final Customer customer = CUSTOMER_DAO.getCustomerByAccountNumber(accountNumber);

			return customer;
		} catch (DatabaseException e) {
			throw new CustomerProcessingException(e.getMessage(), e);
		}
	}

	// Update customer name in Storage using updateCustomerName() method.
	@Override
	public boolean updateCustomerDetail(final String updateData, final String accountNumber, final String updateType)
			throws CustomerProcessingException {
		try {
			return CUSTOMER_DAO.updateCustomerDetails(updateData, accountNumber, updateType);
		} catch (InvalidCustomerNameException | DuplicateEntryException | InvalidCustomerAddressException
				| InvalidCustomerDobException | InvalidMobileNoException | ParseException
				| InvalidCustomerUpdateException | DuplicateUpdateException | DatabaseException e) {
			throw new CustomerProcessingException(e.getMessage(), e);
		}
	}

	// Singleton method for CustomerServiceImpl, ensuring only one instance of the
	// class.
	public final static CustomerService getInstance() {
		if (instance == null) {
			instance = new CustomerServiceImpl();
		}
		return instance;
	}

	// Private constructor to restrict instantiation outside the class, enforcing
	// Singleton pattern.
	private CustomerServiceImpl() {

	}

	private final boolean isUniqueMobile(final String mobileNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return ACCOUNT_DAO.isUniqueMobileForAccount(mobileNumber, accountType);
	}

	private final boolean isUniqueAadhar(final String aadharNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return ACCOUNT_DAO.isUniqueAadharForAccount(aadharNumber, accountType);
	}

	private boolean isUniquePanCardNumber(final String panCardNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return ACCOUNT_DAO.isUniquePanCardNumber(panCardNumber, accountType);
	}
}
