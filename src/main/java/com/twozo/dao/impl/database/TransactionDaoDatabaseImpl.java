package com.twozo.dao.impl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twozo.dao.TransactionDao;
import com.twozo.enums.TransactionColumn;
import com.twozo.enums.TransactionType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.exception.account.MinimumAmountException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.model.Transaction;
import com.twozo.util.DatabaseConnection;

public class TransactionDaoDatabaseImpl implements TransactionDao {

	private static TransactionDaoDatabaseImpl Instance;	

	/* ----- Transaction Services ----- */
	@Override
	public final long depositAmount(final String accountNumber, final double amount) throws DatabaseException {
		final String updateQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ? RETURNING id";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
			conn.setAutoCommit(false);

			updateStatement.setDouble(1, amount);
			updateStatement.setString(2, accountNumber);

			try (ResultSet rs = updateStatement.executeQuery()) {
				if (!rs.next()) {
					conn.rollback();
					return 0;
				}
				conn.commit();
				return rs.getLong("id");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public final long withdrawAmount(final String accountNumber, final double amount)
			throws AccountNotFoundException, MinimumAmountException, DatabaseException {
		final String updateQuery = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ? RETURNING id";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement updateStatement = conn.prepareStatement(updateQuery);) {
			conn.setAutoCommit(false);

			updateStatement.setDouble(1, amount);
			updateStatement.setString(2, accountNumber);

			try (ResultSet rs = updateStatement.executeQuery()) {
				if (!rs.next()) {
					conn.rollback();
					return 0;
				}
				conn.commit();
				return rs.getLong("id");
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public final Map<String, Long> transferFunds(final String senderAccountNumber, final String receiverAccountNumber,
			final double amount) throws InsufficientAmount, DatabaseException {
		final String receiverQuery = "UPDATE Accounts SET balance = balance + ? WHERE account_number = ? RETURNING id";
		final String senderQuery = "UPDATE Accounts SET balance = balance - ? WHERE account_number = ? RETURNING id";

		try (final Connection conn = DatabaseConnection.getConnection();
				final PreparedStatement getSenderStatement = conn.prepareStatement(senderQuery);) {
			conn.setAutoCommit(false);

			getSenderStatement.setDouble(1, amount);
			getSenderStatement.setString(2, senderAccountNumber);

			try (ResultSet firstExecution = getSenderStatement.executeQuery()) {
				if (!firstExecution.next()) {
					conn.rollback();
					return null;
				}
				final long senderTransactionId = firstExecution.getLong(1);

				try (PreparedStatement getReceiverStatement = conn.prepareStatement(receiverQuery)) {
					getReceiverStatement.setDouble(1, amount);
					getReceiverStatement.setString(2, receiverAccountNumber);

					try (ResultSet secondExecution = getReceiverStatement.executeQuery()) {
						if (!secondExecution.next()) {
							conn.rollback();
							return null;
						}
						final long receiverTransactionId = secondExecution.getLong(1);

						Map<String, Long> transactionIds = new HashMap<>();
						transactionIds.put("senderTransactionId", senderTransactionId);
						transactionIds.put("receiverTransactionId", receiverTransactionId);
						conn.commit();

						return transactionIds;
					}
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public final List<Transaction> getTransactionHistory(final String accountNumber) throws DatabaseException {
		final String query = "SELECT t.id, acc.account_number AS account_number, "
				+ "t.date_time, t.amount, t.transaction_type, " + "acc2.account_number AS transfered_account_number "
				+ "FROM transactions t " + "JOIN accounts acc ON t.account_id = acc.id "
				+ "LEFT JOIN accounts acc2 ON t.transfered_account_id = acc2.id " + "WHERE acc.account_number = ?";

		final List<Transaction> transactionHistory = new ArrayList<>();	

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, accountNumber);

			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					final Transaction transaction = new Transaction();

					transaction.setTransactionId(rs.getLong(TransactionColumn.ID.getDescription()));	
					transaction.setAccountNumber(rs.getString(TransactionColumn.ACCOUNT_NUMBER.getDescription()));
					transaction.setTransferedAccountNumber(rs.getString(TransactionColumn.TRANSFERED_ACCOUNT_NUMBER.getDescription()));
					transaction.setDateTime(rs.getTimestamp(TransactionColumn.DATE_TIME.getDescription()).toLocalDateTime());
					transaction.setAmount(rs.getDouble(TransactionColumn.AMOUNT.getDescription()));
					transaction.setTransactionType(TransactionType.valueOf(rs.getString(TransactionColumn.TRANSACTION_TYPE.getDescription())));

					transactionHistory.add(transaction);
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return transactionHistory;
	}

	@Override
	public boolean saveAccountTransaction(final long accountId, final Account account, final double amount,
			final LocalDateTime date, final TransactionType transactionType, final long transferedAccountId)
			throws DatabaseException {
		final String query = "INSERT INTO Transactions (account_id, transfered_account_id, date_time, amount, transaction_type) VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			conn.setAutoCommit(false);
			int count = 1;

			statement.setLong(count++, accountId);
			statement.setLong(count++, transferedAccountId);
			statement.setTimestamp(count++, Timestamp.valueOf(date));
			statement.setDouble(count++, amount);
			statement.setObject(count++, transactionType, Types.OTHER);

			if (!statement.execute()) {
				conn.commit();
				return true;
			} else {
				conn.rollback();
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return false;
	}

	public static final TransactionDaoDatabaseImpl getInstance() {
		if (Instance == null) {
			Instance = new TransactionDaoDatabaseImpl();
		}
		return Instance;
	}
}
