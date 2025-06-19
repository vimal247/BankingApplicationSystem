package com.twozo.dao.impl.collection;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.dao.CustomerDao;
import com.twozo.enums.AccountType;
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

public class CustomerDaoCollectionImpl implements CustomerDao {

	private static CustomerDaoCollectionImpl instance;
	private static long idAutoIncrement = 1;
	
	private static final AccountDao ACCOUNT_DAO = AccountDaoCollectionImpl.getInstance();
	private final static Map<Long, Customer> CUSTOMER_MAP = new HashMap<>();
	

	/* ----- Customer Services ----- */
	@Override
	public long saveCustomer(final Customer customer) {
		long customerId = idAutoIncrement++;
		
		customer.setCustomerId(customerId);
		CUSTOMER_MAP.put(customerId, customer);
		
		return customerId; 
	}

	@Override
	public final Customer getCustomerByAccountNumber(final String accountNumber) throws DatabaseException {
		final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);
		final Customer customer = account.getCustomer();
		
		return customer;
	}

	@Override
	public boolean updateCustomerDetails(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerNameException, DuplicateEntryException, InvalidCustomerAddressException,
			InvalidCustomerDobException, InvalidMobileNoException, ParseException, InvalidCustomerUpdateException,
			DuplicateUpdateException, DatabaseException {
		return updateDetail(updateData, accountNumber, updateType);

	}

	private boolean updateDetail(final String updateData, final String identity, final String updateType)
			throws InvalidCustomerNameException, DuplicateEntryException, InvalidCustomerAddressException,
			InvalidCustomerDobException, InvalidMobileNoException, ParseException, DatabaseException {

		if (updateType.equalsIgnoreCase("name")) {
			return updateCustomerName(updateData, identity);
		}
		if (updateType.equalsIgnoreCase("mobile_Number")) {
			return updateCustomerMobileNumber(updateData, identity);
		}
		if (updateType.equalsIgnoreCase("dob")) {
			return updateCustomerDob(updateData, identity);
		}
		if (updateType.equalsIgnoreCase("address")) {
			return updateCustomerAddress(updateData, identity);
		}
		return false;
	}

	private boolean updateCustomerName(final String name, final String accountNumber)
			throws InvalidCustomerNameException, DatabaseException {
		final Customer customer = ACCOUNT_DAO.getAccountByNumber(accountNumber).getCustomer();

		if (customer != null) {
			customer.setName(name);
			return true;
		}
		return false;
	}

	// Update customer mobile number in storage, after checking for uniqueness.
	private boolean updateCustomerMobileNumber(final String mobileNumber, final String accountNumber)
			throws DuplicateEntryException, InvalidMobileNoException, DatabaseException {
		final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);
		final AccountType accountType = account.getAccountType();

		if (account != null) {
			if (ACCOUNT_DAO.isUniqueMobileForAccount(mobileNumber, accountType)) {
				final Customer customer = ACCOUNT_DAO.getAccountByNumber(accountNumber).getCustomer();
				customer.setMobileNumber(mobileNumber);
				return true;
			}
		}
		return false;
	}

	// Update customer DOB in Storage using updateCustomerDob() method.
	private boolean updateCustomerDob(final String dob, final String accountNumber)
			throws InvalidCustomerDobException, ParseException, DatabaseException {
		final Customer customer = ACCOUNT_DAO.getAccountByNumber(accountNumber).getCustomer();

		if (customer != null) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			final LocalDate dobDate = LocalDate.parse(dob, formatter);
			customer.setDob(dobDate);
			return true;
		}
		return false;
	}

	// Update customer address in Storage using updateCustomerAddress() method.
	private boolean updateCustomerAddress(final String address, final String accountNumber)
			throws InvalidCustomerAddressException, DatabaseException {
		final Customer customer = ACCOUNT_DAO.getAccountByNumber(accountNumber).getCustomer();

		if (customer != null) {
			customer.setAddress(address);
			return true;
		}
		return false;
	}

	@Override
	public Customer getCustomerById(final long customerId) {
		return CUSTOMER_MAP.get(customerId);
	}

	public static final CustomerDaoCollectionImpl getInstance() {
		if (instance == null) {
			instance = new CustomerDaoCollectionImpl();
		}
		return instance;
	}
}
