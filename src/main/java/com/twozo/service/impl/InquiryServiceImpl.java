package com.twozo.service.impl;

import com.twozo.dao.InquiryDao;
import com.twozo.dao.impl.database.InquiryDaoDatabaseImpl;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.inquiry.InquiryProcessingException;
import com.twozo.service.InquiryService;

public class InquiryServiceImpl implements InquiryService {
	
	private static InquiryService instance;
	private static final InquiryDao INQUIRY_DAO = InquiryDaoDatabaseImpl.getInstance(); 
	
	// Override InquiryService abstract method.
	@Override 
	public double getBalance(final String accountNumber) throws InquiryProcessingException {
		
		try {
			return INQUIRY_DAO.getBalance(accountNumber);
		} catch (DatabaseException e) { 
			throw new InquiryProcessingException(e.getMessage(), e);
		}
	}
	
	// Singleton method for InquiryService, ensuring only one instance of the class.
	public static final InquiryService getInstance() {
		if (instance == null) {
			instance = new InquiryServiceImpl();
		}
		return instance;
	}
	
	// Private constructor to restrict instantiation outside the class, enforcing Singleton pattern.
	private InquiryServiceImpl() {

	}
}
