package com.twozo.dao.impl.database;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.twozo.dao.CustomerDao;
import com.twozo.exception.customer.CustomerNotFoundException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Customer;
import com.twozo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerDaoDatabaseImpl implements CustomerDao {

	private CustomerRepository customerRepository;

	@Autowired
	public CustomerDaoDatabaseImpl(final CustomerRepository customerRepository){
		this.customerRepository = customerRepository;
	}

	/* ----- Customer Services ----- */
	@Override
	public long saveCustomer(final Customer customer) {
		return customerRepository.save(customer).getCustomerId();
	}

	@Override
	public final Customer getCustomerByAccountNumber(final String accountNumber) throws CustomerNotFoundException {
		final Customer customer = customerRepository.findCustomerByAccountNumber(accountNumber);

		if (customer == null) {
			throw new CustomerNotFoundException("Customer not found for account number: ");
		}
		return customer;}

	@Override
	public final Customer getCustomerById(final long customerId) throws CustomerNotFoundException {
		final Customer customer = customerRepository.findById(String.valueOf(customerId)).orElse(null);

		if (customer == null) {
			throw new CustomerNotFoundException("Customer not found with ID: ");
		}
		return customer;
	}

	@Override
	public boolean updateCustomerDetails(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerUpdateException, DuplicateUpdateException, DatabaseException {
		if (!isValidKey(updateType)) {
			throw new InvalidCustomerUpdateException("Invalid update type: " + updateType);
		}

		final Customer customer = customerRepository.findCustomerByAccountNumber(accountNumber);
		if (customer == null) {
			return false;
		}

		switch (updateType.toLowerCase()) {
			case "name":
				if (customer.getName().equals(updateData)) {
					throw new DuplicateUpdateException("New value is the same as the existing value. No update needed.");
				}
				customer.setName(updateData);
				break;

			case "mobile_number":
				if (customer.getMobileNumber().equals(updateData)) {
					throw new DuplicateUpdateException("New value is the same as the existing value. No update needed.");
				}
				customer.setMobileNumber(updateData);
				break;

			case "dob":
				final LocalDate newDob = LocalDate.parse(updateData, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				if (customer.getDob().equals(newDob)) {
					throw new DuplicateUpdateException("New DOB is the same as the existing DOB. No update needed.");
				}
				customer.setDob(newDob);
				break;

			case "address":
				if (customer.getAddress().equals(updateData)) {
					throw new DuplicateUpdateException("New value is the same as the existing value. No update needed.");
				}
				customer.setAddress(updateData);
				break;

			default:
				throw new InvalidCustomerUpdateException("Invalid update type: " + updateType);
		}

		customerRepository.save(customer);
		return true;
	}

	private boolean isValidKey(String key) {
		return key.equalsIgnoreCase("name") || key.equalsIgnoreCase("mobile_number") || key.equalsIgnoreCase("dob")
				|| key.equalsIgnoreCase("address");
	}

	private CustomerDaoDatabaseImpl() {

	}
}
