package com.twozo.dao.impl.collection;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.dao.TransactionDao;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.exception.account.MinimumAmountException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.model.Transaction;

public class TransactionDaoCollectionImpl implements TransactionDao {

	private static TransactionDaoCollectionImpl instance;

	long transactionId = 1; 
			
	private static final AccountDao ACCOUNT_DAO = AccountDaoCollectionImpl.getInstance();

	@Override
	public final long depositAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException {
		final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);
		
		if (account != null) {
			account.setBalance(account.getBalance() + amount);
		}
		return transactionId++;
	}

	@Override
	public final long withdrawAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException {
		final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);
		
		if (account != null) {
			account.setBalance(account.getBalance() + amount);
		}
		return transactionId++; 
	}

	@Override
	public final Map<String, Long> transferFunds(final String senderAccountNumber, final String receiverAccountNumber, final double amount)
			throws InsufficientAmount, DatabaseException {
		long senderTransactionId = 0;
		long receiverTransactionId = 0;
		
		final Account senderAccount = ACCOUNT_DAO.getAccountByNumber(senderAccountNumber);
		final Account receiverAccount = ACCOUNT_DAO.getAccountByNumber(receiverAccountNumber);
		
		senderAccount.setBalance(senderAccount.getBalance() - amount);
		receiverAccount.setBalance(senderAccount.getBalance() + amount);
		
		Map<String, Long> transactionIds = new HashMap<>();
		transactionIds.put("senderTransactionId", senderTransactionId);
		transactionIds.put("receiverTransactionId", receiverTransactionId);
		
		return transactionIds;  
	}

	@Override
	public final List<Transaction> getTransactionHistory(final String accountNumber) throws DatabaseException {
		final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);

		return account.getTransaction();
	} 
	
	@Override
	public final boolean saveAccountTransaction(final long transactionId, final Account account, final double amount,
			final LocalDateTime date, final TransactionType transactionType, final long transferAccountId) {
		final List<Transaction> transactionHistory = account.getTransaction();
		final Transaction newTransactionRecord = new Transaction(transactionId, account.getAccountNumber(), amount,
				date, transactionType, null); 

		if (transactionId != 0) {
			transactionHistory.add(newTransactionRecord);
			return true;
		}
		return false;
	}

	public static final TransactionDaoCollectionImpl getInstance() {
		if (instance == null) {
			instance = new TransactionDaoCollectionImpl();
		}
		return instance;
	}
}
