package com.twozo.service.impl;

import com.twozo.dao.InquiryDao;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.inquiry.InquiryProcessingException;
import com.twozo.exception.inquiry.NoBalanceFoundException;
import com.twozo.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InquiryServiceImpl implements InquiryService {

	private  InquiryDao inquiryDao;

	@Autowired
	public InquiryServiceImpl(final InquiryDao inquiryDao) {
		this.inquiryDao = inquiryDao;
	}
	
	// Override InquiryService abstract method.
	@Override 
	public double getBalance(final String accountNumber) throws InquiryProcessingException {
		
		try {
			return inquiryDao.getBalance(accountNumber);
		} catch (NoBalanceFoundException | DatabaseException | AccountNotFoundException e) {
			throw new InquiryProcessingException(e.getMessage(), e);
		}
	}
	
	// Private constructor to restrict instantiation outside the class, enforcing Singleton pattern.
	private InquiryServiceImpl() {

	}
}
