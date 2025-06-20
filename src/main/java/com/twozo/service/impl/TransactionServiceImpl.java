package com.twozo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.dao.TransactionDao;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.exception.account.MinimumAmountException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.transaction.TransactionProcessingException;
import com.twozo.model.Account;
import com.twozo.model.Transaction;
import com.twozo.service.TransactionService;
import com.twozo.validation.TransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static final double MIN_AMOUNT = 100;
	private static final double MIN_BALANCE = 500;

	private AccountDao accountDao;
	private TransactionDao transactionDao;
	private TransactionValidator transactionValidator;

	@Autowired
	public TransactionServiceImpl(final AccountDao accountDao,final TransactionDao transactionDao, final TransactionValidator transactionValidator) {
		this.accountDao = accountDao;
		this.transactionDao = transactionDao;
		this.transactionValidator = transactionValidator;
	}

	@Override
	public final boolean depositAmount(final String accountNumber, final double amount)
			throws TransactionProcessingException {
		try {
			if (MIN_AMOUNT > amount) {
				throw new InsufficientAmount("minimum deposit amount is above 100");
			}

			final Account account = accountDao.getAccountByNumber(accountNumber);

			if (transactionValidator.validateDepositAmount(account, amount)) {
				final long accountId = transactionDao.depositAmount(accountNumber, amount);

				if (accountId != 0) {
					return transactionDao.saveAccountTransaction(accountId, account, amount, LocalDateTime.now(),
							TransactionType.DEPOSIT, 0);
				}
			}
			return false;
		} catch (MinimumAmountException | AccountNotFoundException | DatabaseException | InsufficientAmount e) {
			throw new TransactionProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public final boolean withdrawAmount(final String accountNumber, final double amount)
			throws TransactionProcessingException {
		try {
			if (MIN_AMOUNT > amount) {
				throw new InsufficientAmount("minimum withdraw amount is above 100");
			}

			final Account account = accountDao.getAccountByNumber(accountNumber);

			if (transactionValidator.validateWithdrawAmount(account, amount)) {
				final long accountId = transactionDao.withdrawAmount(accountNumber, amount);

				if (accountId != 0) {
					return transactionDao.saveAccountTransaction(accountId, account, amount, LocalDateTime.now(),
							TransactionType.WITHDRAW, 0);
				}
			}
			return false;
		} catch (MinimumAmountException | AccountNotFoundException | InsufficientAmount | DatabaseException e) {
			throw new TransactionProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public final boolean transferFunds(final String senderAccountNumber, final double amount,
			final String receiverAccountNumber) throws TransactionProcessingException {
		try {
			if (MIN_AMOUNT > amount) {
				throw new InsufficientAmount("minimum transfer amount is above 100");
			}

			final Account senderAccount = accountDao.getAccountByNumber(senderAccountNumber);
			final Account receiverAccount = accountDao.getAccountByNumber(receiverAccountNumber);

			if (senderAccount.getBalance() - amount >= MIN_BALANCE) {
				final Map<String, Long> transactionIds = transactionDao.transferFunds(senderAccountNumber,
						receiverAccountNumber, amount);

				if (transactionIds != null) {
					final LocalDateTime date = LocalDateTime.now();

					final long senderTransactionId = transactionIds.get("senderTransactionId");
					final long receiverTransactionId = transactionIds.get("receiverTransactionId");

					return transactionDao.saveAccountTransaction(senderTransactionId, receiverAccount, amount, date,
							TransactionType.DEBIT, receiverTransactionId)
							&& transactionDao.saveAccountTransaction(receiverTransactionId, senderAccount, amount,
									date, TransactionType.CREDIT, senderTransactionId);	
				}
			}
			throw new InsufficientAmount("Insufficient Balance in " + senderAccountNumber);
		} catch (InsufficientAmount | DatabaseException | AccountNotFoundException e) {
			throw new TransactionProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public final List<Transaction> getTransactionHistory(final String accountNumber)
			throws TransactionProcessingException {
		try {
			return transactionDao.getTransactionHistory(accountNumber);
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new TransactionProcessingException(e.getMessage(), e);
		}
	}

	private TransactionServiceImpl() {

	}
}
