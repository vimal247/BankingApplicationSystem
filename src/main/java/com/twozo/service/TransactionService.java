package com.twozo.service;

import java.util.List;

import com.twozo.exception.transaction.TransactionProcessingException;
import com.twozo.model.Transaction;

public interface TransactionService {

	boolean depositAmount(final String accountNo, final double amount) throws TransactionProcessingException;

	boolean withdrawAmount(final String accountNo, final double amount) throws TransactionProcessingException;

	boolean transferFunds(final String senderAccountNo, final double money, final String receiverAccountNo)
			throws TransactionProcessingException;

	List<Transaction> getTransactionHistory(final String accountNo)
			throws TransactionProcessingException;
}
