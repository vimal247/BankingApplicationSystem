package com.twozo.service;

import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.model.Account;
import com.twozo.model.Customer;

public interface CustomerService {
	
	long saveCustomerFromAccount(final Account account) throws CustomerProcessingException;

	Customer getCustomerByAccountNumber(final String accountNumber) throws CustomerProcessingException;

	boolean updateCustomerDetail(final String updateData, final String accountNumber, final String key)
			throws CustomerProcessingException;
}
