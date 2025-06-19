package com.twozo.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.exception.account.MinimumAmountException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.model.Transaction;

public interface TransactionDao {

	/* ----- Transaction Services ----- */
	long depositAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException;

	long withdrawAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException;

	List<Transaction> getTransactionHistory(final String accountNumber) throws DatabaseException;

	Map<String, Long> transferFunds(final String senderAccountNumber, final String receiverAccountNumber, final double amount)
			throws InsufficientAmount, DatabaseException;

	boolean saveAccountTransaction(final long accountId, final Account account, final double amount,
			final LocalDateTime date, final TransactionType transactionType, final long transferedAccountId) throws DatabaseException;

}
