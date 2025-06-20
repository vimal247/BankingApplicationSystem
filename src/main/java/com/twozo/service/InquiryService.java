package com.twozo.service;

import com.twozo.exception.inquiry.InquiryProcessingException;

public interface InquiryService {
	
	double getBalance(final String accountNo) throws InquiryProcessingException;
}
