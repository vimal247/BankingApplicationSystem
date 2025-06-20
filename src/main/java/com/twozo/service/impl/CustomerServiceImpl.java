package com.twozo.service.impl;

import java.text.ParseException;

import com.twozo.dao.AccountDao;
import com.twozo.dao.CustomerDao;
import com.twozo.dao.impl.database.AccountDaoDatabaseImpl;
import com.twozo.dao.impl.database.CustomerDaoDatabaseImpl;
import com.twozo.enums.AccountType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.customer.*;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class CustomerServiceImpl implements CustomerService {

	private final CustomerDao customerDao;
	private final AccountDao accountDao;

	@Autowired
	public  CustomerServiceImpl(final AccountDaoDatabaseImpl accountDao, final CustomerDaoDatabaseImpl customerDao){
		this.accountDao = accountDao;
		this.customerDao = customerDao;
	}
	// Save customer data in storage after performing uniqueness checks for mobile number and Aadhar number.
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

			return customerDao.saveCustomer(customer);

		} catch (DatabaseException | DuplicateEntryException e) {
			throw new CustomerProcessingException(e.getMessage(), e);
		}
	}

	// Retrieve customer data from Storage.
	@Override
	public final Customer getCustomerByAccountNumber(final String accountNumber) throws CustomerProcessingException {
		try {
			return customerDao.getCustomerByAccountNumber(accountNumber);
		} catch (CustomerNotFoundException | DatabaseException | AccountNotFoundException e) {
			throw new CustomerProcessingException(e.getMessage(), e);
		}
	}

	// Update customer name in Storage using updateCustomerName() method.
	@Override
	public boolean updateCustomerDetail(final String updateData, final String accountNumber, final String updateType)
			throws CustomerProcessingException {
		try {
			return customerDao.updateCustomerDetails(updateData, accountNumber, updateType);
		} catch (InvalidCustomerNameException | DuplicateEntryException | InvalidCustomerAddressException |
                 InvalidCustomerDobException | InvalidMobileNoException | ParseException |
                 InvalidCustomerUpdateException | DuplicateUpdateException | DatabaseException |
                 AccountNotFoundException e) {
			throw new CustomerProcessingException(e.getMessage(), e);
		}
	}

	private boolean isUniqueMobile(final String mobileNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return accountDao.isUniqueMobileForAccount(mobileNumber, accountType);
	}

	private boolean isUniqueAadhar(final String aadharNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return accountDao.isUniqueAadharForAccount(aadharNumber, accountType);
	}

	private boolean isUniquePanCardNumber(final String panCardNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return accountDao.isUniquePanCardNumber(panCardNumber, accountType);
	}
}
