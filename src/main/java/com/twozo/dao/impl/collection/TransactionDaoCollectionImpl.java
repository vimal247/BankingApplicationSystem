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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDaoCollectionImpl implements TransactionDao {

	long transactionId = 1; 
			
	private final AccountDao accountDao;

	@Autowired
	public TransactionDaoCollectionImpl(final AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	@Override
	public final long depositAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException {
		final Account account = accountDao.getAccountByNumber(accountNumber);
		
		if (account != null) {
			account.setBalance(account.getBalance() + amount);
		}
		return transactionId++;
	}

	@Override
	public final long withdrawAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException {
		final Account account = accountDao.getAccountByNumber(accountNumber);
		
		if (account != null) {
			account.setBalance(account.getBalance() + amount);
		}
		return transactionId++; 
	}

	@Override
	public final Map<String, Long> transferFunds(final String senderAccountNumber, final String receiverAccountNumber, final double amount)
            throws InsufficientAmount, DatabaseException, AccountNotFoundException {
		long senderTransactionId = 0;
		long receiverTransactionId = 0;
		
		final Account senderAccount = accountDao.getAccountByNumber(senderAccountNumber);
		final Account receiverAccount = accountDao.getAccountByNumber(receiverAccountNumber);
		
		senderAccount.setBalance(senderAccount.getBalance() - amount);
		receiverAccount.setBalance(senderAccount.getBalance() + amount);
		
		Map<String, Long> transactionIds = new HashMap<>();
		transactionIds.put("senderTransactionId", senderTransactionId);
		transactionIds.put("receiverTransactionId", receiverTransactionId);
		
		return transactionIds;  
	}

	@Override
	public final List<Transaction> getTransactionHistory(final String accountNumber) throws DatabaseException, AccountNotFoundException {
		final Account account = accountDao.getAccountByNumber(accountNumber);

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
}
