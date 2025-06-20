package com.twozo.dao.impl.database;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twozo.dao.TransactionDao;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.model.Account;
import com.twozo.model.Transaction;
import com.twozo.repository.AccountRepository;
import com.twozo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionDaoDatabaseImpl implements TransactionDao {

	private TransactionRepository transactionRepository;
	private AccountRepository accountRepository;

	@Autowired
	public TransactionDaoDatabaseImpl(final TransactionRepository transactionRepository, final AccountRepository accountRepository) {
		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
	}

	/* ----- Transaction Services ----- */
	@Override
	public final long depositAmount(final String accountNumber, final double amount) throws AccountNotFoundException {
		final Account account = transactionRepository.findByAccountNumber(accountNumber);

		if (account == null) {
			throw new AccountNotFoundException("Account not found");
		}

		account.setBalance(account.getBalance() + amount);
		accountRepository.save(account);

		return account.getId();
	}

	@Override
	public final long withdrawAmount(final String accountNumber, final double amount) throws AccountNotFoundException {
		final Account account = transactionRepository.findByAccountNumber(accountNumber);

		if (account == null) {
			throw new AccountNotFoundException("Account not found");
		}

		account.setBalance(account.getBalance() - amount);
		accountRepository.save(account);

		return account.getId();
	}

	@Override
	public final Map<String, Long> transferFunds(final String senderAccountNumber, final String receiverAccountNumber,
												 final double amount) throws AccountNotFoundException, InsufficientAmount {
		final Account sender = accountRepository.findByAccountNumber(senderAccountNumber);

		if (sender == null) {
			throw new AccountNotFoundException("Sender account not found");
		}

		final Account receiver = accountRepository.findByAccountNumber(receiverAccountNumber);

		if (receiver == null) {
			throw new AccountNotFoundException("Receiver account not found");
		}

		if (sender.getBalance() < amount) {
			throw new InsufficientAmount("Insufficient balance in sender's account");
		}

		sender.setBalance(sender.getBalance() - amount);
		receiver.setBalance(receiver.getBalance() + amount);

		accountRepository.save(sender);
		accountRepository.save(receiver);

		final Map<String, Long> transactionIds = new HashMap<>();

		transactionIds.put("senderTransactionId", sender.getId());
		transactionIds.put("receiverTransactionId", receiver.getId());

		return transactionIds;
	}

	@Override
	public final List<Transaction> getTransactionHistory(final String accountNumber) throws AccountNotFoundException {
		final Account account = transactionRepository.findByAccountNumber(accountNumber);

		if (account == null) {
			throw new AccountNotFoundException("Account not found");
		}
		return account.getTransaction();
	}

	@Override
	public boolean saveAccountTransaction(final long accountId, final Account account, final double amount,
			final LocalDateTime date, final TransactionType transactionType, final long transferedAccountId) {

		final Transaction transaction = new Transaction();

		transaction.setTransactionId(accountId);
		transaction.setTransferedAccountId(transferedAccountId);
		transaction.setAccountNumber(account.getAccountNumber());
		transaction.setDateTime(date);
		transaction.setAmount(amount);
		transaction.setTransactionType(transactionType);

		transactionRepository.save(transaction);

		return true;
	}

	private  TransactionDaoDatabaseImpl() {

	}
}
