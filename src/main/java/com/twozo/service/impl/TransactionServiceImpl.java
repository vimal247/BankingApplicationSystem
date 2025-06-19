package com.twozo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.dao.TransactionDao;
import com.twozo.dao.impl.database.AccountDaoDatabaseImpl;
import com.twozo.dao.impl.database.TransactionDaoDatabaseImpl;
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

public class TransactionServiceImpl implements TransactionService {

	private static final double MIN_AMOUNT = 100;
	private static final double MIN_BALANCE = 500;
	private static TransactionService instance;

	private static final AccountDao ACCOUNT_DAO = AccountDaoDatabaseImpl.getInstance();
	private static final TransactionDao TRANSACTION_DAO = TransactionDaoDatabaseImpl.getInstance();
	private static final TransactionValidator TRANSACTION_VALIDATOR = TransactionValidator.getInstance();

	@Override
	public final boolean depositAmount(final String accountNumber, final double amount)
			throws TransactionProcessingException {
		try {
			if (MIN_AMOUNT > amount) {
				throw new InsufficientAmount("minimum deposit amount is above 100");
			}

			final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);

			if (TRANSACTION_VALIDATOR.validateDepositAmount(account, amount)) {
				final long accountId = TRANSACTION_DAO.depositAmount(accountNumber, amount);

				if (accountId != 0) {
					return TRANSACTION_DAO.saveAccountTransaction(accountId, account, amount, LocalDateTime.now(),
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

			final Account account = ACCOUNT_DAO.getAccountByNumber(accountNumber);

			if (TRANSACTION_VALIDATOR.validateWithdrawAmount(account, amount)) {
				final long accountId = TRANSACTION_DAO.withdrawAmount(accountNumber, amount);

				if (accountId != 0) {
					return TRANSACTION_DAO.saveAccountTransaction(accountId, account, amount, LocalDateTime.now(),
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

			final Account senderAccount = ACCOUNT_DAO.getAccountByNumber(senderAccountNumber);
			final Account receiverAccount = ACCOUNT_DAO.getAccountByNumber(receiverAccountNumber);

			if (senderAccount.getBalance() - amount >= MIN_BALANCE) {
				final Map<String, Long> transactionIds = TRANSACTION_DAO.transferFunds(senderAccountNumber,
						receiverAccountNumber, amount);

				if (transactionIds != null) {
					final LocalDateTime date = LocalDateTime.now();

					final long senderTransactionId = transactionIds.get("senderTransactionId");
					final long receiverTransactionId = transactionIds.get("receiverTransactionId");

					return TRANSACTION_DAO.saveAccountTransaction(senderTransactionId, receiverAccount, amount, date,
							TransactionType.DEBIT, receiverTransactionId)
							&& TRANSACTION_DAO.saveAccountTransaction(receiverTransactionId, senderAccount, amount,
									date, TransactionType.CREDIT, senderTransactionId);	
				}
			}
			throw new InsufficientAmount("Insufficient Balance in " + senderAccountNumber);
		} catch (InsufficientAmount | DatabaseException e) {
			throw new TransactionProcessingException(e.getMessage(), e);
		}
	}

	@Override
	public final List<Transaction> getTransactionHistory(final String accountNumber)
			throws TransactionProcessingException {
		try {
			return TRANSACTION_DAO.getTransactionHistory(accountNumber);
		} catch (DatabaseException e) {
			throw new TransactionProcessingException(e.getMessage(), e);
		}
	}

	public static final TransactionService getInstance() {
		if (instance == null) {
			instance = new TransactionServiceImpl();
		}
		return instance;
	}

	private TransactionServiceImpl() {

	}
}
