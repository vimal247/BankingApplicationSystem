package com.twozo.dao.impl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.twozo.dao.AccountDao;
import com.twozo.enums.AccountColumn;
import com.twozo.enums.AccountStatus;
import com.twozo.enums.AccountType;
import com.twozo.enums.BranchCode;
import com.twozo.enums.BranchName;
import com.twozo.enums.CustomerColumn;
import com.twozo.enums.Gender;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.util.DatabaseConnection;

public class AccountDaoDatabaseImpl implements AccountDao {

	private static AccountDaoDatabaseImpl Instance;
	// private static final CustomerDao customerDao =
	// CustomerDaoDatabaseImpl.getInstance();

	/* ----- Account Services ----- */
	@Override
	public long saveAccount(final long customerId, final Account account)
			throws DuplicateEntryException, DatabaseException {
		if (isUniqueAccountNumber(account.getAccountNumber())) {
			final String query = "INSERT INTO Accounts (customer_id, bank_name, account_number, ifsc_code, branch_name, account_type,"
					+ " balance, account_status, branch_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

			try (Connection conn = DatabaseConnection.getConnection();
					PreparedStatement statement = conn.prepareStatement(query)) {
				conn.setAutoCommit(false);
				int count = 1;

				statement.setLong(count++, customerId);
				statement.setObject(count++, Account.getBankName(), Types.OTHER);
				statement.setString(count++, account.getAccountNumber());
				statement.setString(count++, account.getIfscCode());
				statement.setObject(count++, account.getBranchName(), Types.OTHER);
				statement.setObject(count++, account.getAccountType(), Types.OTHER);
				statement.setDouble(count++, account.getBalance());
				statement.setObject(count++, account.getAccountStatus(), Types.OTHER);
				statement.setObject(count++, account.getBranchCode().getCode(), Types.OTHER);	

				try (ResultSet rs = statement.executeQuery()) {
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
		return 0;
	}

	@Override
	public final Account getAccountByNumber(final String accountNumber) throws DatabaseException {
		final String query = "SELECT * FROM Accounts a " + "INNER JOIN Customers c ON a.customer_id = c.id "
				+ "WHERE a.account_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, accountNumber);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					final Account account = new Account();

					account.setBranchName(BranchName.valueOf(rs.getString(AccountColumn.BRANCH_NAME.getDescription())));
					account.setBranchCode(BranchCode.fromCode(rs.getInt(AccountColumn.BRANCH_CODE.getDescription())));
					account.setAccountNumber(rs.getString(AccountColumn.ACCOUNT_NUMBER.getDescription()));
					account.setIfscCode(rs.getString(AccountColumn.IFSC_CODE.getDescription()));
					account.setAccountType(AccountType.valueOf(rs.getString(AccountColumn.ACCOUNT_TYPE.getDescription())));
					account.setBalance(rs.getDouble(AccountColumn.BALANCE.getDescription()));
					account.setAccountStatus(AccountStatus.valueOf(rs.getString(AccountColumn.ACCOUNT_STATUS.getDescription())));

					final Customer customer = new Customer();

					customer.setName(rs.getString(CustomerColumn.NAME.getDescription()));
					customer.setCustomerId(rs.getLong(AccountColumn.CUSTOMER_ID.getDescription()));
					customer.setMobileNumber(rs.getString(CustomerColumn.MOBILE_NUMBER.getDescription()));
					customer.setDob(rs.getDate(CustomerColumn.DOB.getDescription()).toLocalDate());
					customer.setPanCardNumber(rs.getString(CustomerColumn.PAN_CARD_NUMBER.getDescription()));
					customer.setPhotoUrl(rs.getString(CustomerColumn.PHOTO_URL.getDescription()));
					customer.setAadharNumber(rs.getString(CustomerColumn.AADHAR_NUMBER.getDescription()));
					customer.setAddress(rs.getString(CustomerColumn.ADDRESS.getDescription()));
					customer.setGender(Gender.valueOf(rs.getString(CustomerColumn.GENDER.getDescription())));

					account.setCustomer(customer);

					return account;
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public boolean updateAccountDetail(final String accountNumber, final AccountType accountType)
			throws DatabaseException {
		final String query = "UPDATE Accounts SET account_type = ? WHERE account_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			conn.setAutoCommit(false);

			statement.setObject(1, accountType, Types.OTHER);
			statement.setString(2, accountNumber);

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

	@Override
	public boolean deactivateAccount(final String accountNumber) throws DatabaseException {
		final String query = "UPDATE Accounts SET account_status = ? WHERE account_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			conn.setAutoCommit(false);

			statement.setObject(1, AccountStatus.INACTIVE, Types.OTHER);
			statement.setString(2, accountNumber);

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

	private boolean isUniqueAccountNumber(final String accountNumber)
			throws DuplicateEntryException, DatabaseException {
		final String query = "SELECT COUNT(*) FROM accounts WHERE account_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, accountNumber);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					throw new DuplicateEntryException("Account number already exists.");
				}
			}
			return true;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public final boolean isUniquePanCardNumber(final String panCardNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return isUniqueForAccountType("pan_card_number", panCardNumber, accountType);
	}

	@Override
	public boolean isUniqueAadharForAccount(final String aadharNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return isUniqueForAccountType("aadhar_number", aadharNumber, accountType);
	}

	@Override
	public boolean isUniqueMobileForAccount(final String mobileNumber, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {

		return isUniqueForAccountType("mobile_number", mobileNumber, accountType);
	}

	private boolean isUniqueForAccountType(final String columnName, final String value, final AccountType accountType)
			throws DuplicateEntryException, DatabaseException {
		final String query = "SELECT COUNT(*) FROM Customers c " + "JOIN Accounts a ON c.id = a.customer_id "
				+ "WHERE c." + columnName + " = ? AND a.account_type = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, value);
			statement.setObject(2, accountType, Types.OTHER);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					throw new DuplicateEntryException("Entry already exists for account type: " + accountType);
				}
				return true;
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	public static final AccountDaoDatabaseImpl getInstance() {
		if (Instance == null) {
			Instance = new AccountDaoDatabaseImpl();
		}
		return Instance;
	}
}
