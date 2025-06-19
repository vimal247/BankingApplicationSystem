package com.twozo.dao.impl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.twozo.dao.CustomerDao;
import com.twozo.enums.CustomerColumn;
import com.twozo.enums.Gender;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerAddressException;
import com.twozo.exception.customer.InvalidCustomerDobException;
import com.twozo.exception.customer.InvalidCustomerNameException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.customer.InvalidMobileNoException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.model.Customer;
import com.twozo.util.DatabaseConnection;

public class CustomerDaoDatabaseImpl implements CustomerDao {

	private static CustomerDaoDatabaseImpl instance;

	/* ----- Customer Services ----- */
	@Override
	public long saveCustomer(final Customer customer) throws DatabaseException {
		final String query = "INSERT INTO Customers (name, gender, mobile_number, "
				+ "aadhar_number, pan_card_number, dob, address, photo_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query);) {
			conn.setAutoCommit(false);
			int count = 1;

			statement.setString(count++, customer.getName());
			statement.setObject(count++, customer.getGender(), Types.OTHER);
			statement.setString(count++, customer.getMobileNumber());
			statement.setString(count++, customer.getAadharNumber());
			statement.setString(count++, customer.getPanCardNumber());
			statement.setDate(count++, java.sql.Date.valueOf(customer.getDob()));
			statement.setString(count++, customer.getAddress());
			statement.setString(count++, customer.getPhotoUrl());

			try (ResultSet rs = statement.executeQuery()) {
				if (!rs.next()) {
					conn.rollback();
					return 0;
				}
				conn.commit();
				return rs.getLong("id");
			}
		} catch (SQLException e) {
			throw new DatabaseException("Database error : " + e.getMessage(), e);
		}
	}

	@Override
	public final Customer getCustomerByAccountNumber(final String accountNumber) throws DatabaseException {
		final String query = "SELECT * FROM Customers WHERE id = "
				+ "(SELECT customer_id FROM Accounts WHERE account_number = ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, accountNumber);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					final Customer customer = new Customer();

					customer.setName(rs.getString(CustomerColumn.NAME.getDescription()));
					customer.setCustomerId(rs.getLong(CustomerColumn.ID.getDescription()));
					customer.setMobileNumber(rs.getString(CustomerColumn.MOBILE_NUMBER.getDescription()));
					customer.setDob(rs.getDate(CustomerColumn.DOB.getDescription()).toLocalDate());
					customer.setPanCardNumber(rs.getString(CustomerColumn.PAN_CARD_NUMBER.getDescription()));
					customer.setPhotoUrl(rs.getString(CustomerColumn.PHOTO_URL.getDescription()));
					customer.setAadharNumber(rs.getString(CustomerColumn.AADHAR_NUMBER.getDescription()));
					customer.setAddress(rs.getString(CustomerColumn.ADDRESS.getDescription()));
					customer.setGender(Gender.valueOf(rs.getString(CustomerColumn.GENDER.getDescription())));

					return customer;
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("Database error : ", e);
		}
		return null;
	}

	@Override
	public final Customer getCustomerById(final long customerId) throws DatabaseException {
		final String query = "SELECT * FROM Customers WHERE customer_id = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, customerId);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					final Customer customer = new Customer();

					customer.setName(rs.getString(CustomerColumn.NAME.getDescription()));
					customer.setCustomerId(rs.getLong(CustomerColumn.ID.getDescription()));
					customer.setMobileNumber(rs.getString(CustomerColumn.MOBILE_NUMBER.getDescription()));
					customer.setDob(rs.getDate(CustomerColumn.DOB.getDescription()).toLocalDate());
					customer.setPanCardNumber(rs.getString(CustomerColumn.PAN_CARD_NUMBER.getDescription()));
					customer.setPhotoUrl(rs.getString(CustomerColumn.PHOTO_URL.getDescription()));
					customer.setAadharNumber(rs.getString(CustomerColumn.AADHAR_NUMBER.getDescription()));
					customer.setAddress(rs.getString(CustomerColumn.ADDRESS.getDescription()));
					customer.setGender(Gender.valueOf(rs.getString(CustomerColumn.GENDER.getDescription())));

					return customer;
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("Database error : ", e);
		}
		return null;
	}

	@Override
	public boolean updateCustomerDetails(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerNameException, DuplicateEntryException, InvalidCustomerAddressException,
			InvalidCustomerDobException, InvalidMobileNoException, ParseException, InvalidCustomerUpdateException,
			DuplicateUpdateException, DatabaseException {
		return updateDetail(updateData, accountNumber, updateType);
	}

	private boolean isValidKey(String key) {
		return key.equalsIgnoreCase("name") || key.equalsIgnoreCase("mobile_number") || key.equalsIgnoreCase("dob")
				|| key.equalsIgnoreCase("address");
	}

	private boolean updateDetail(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerNameException, DuplicateEntryException, InvalidCustomerAddressException,
			InvalidCustomerDobException, InvalidMobileNoException, ParseException, InvalidCustomerUpdateException,
			DuplicateUpdateException, DatabaseException {
		if (!isValidKey(updateType)) {
			throw new InvalidCustomerUpdateException(updateType);
		}

		final String getQuery = "SELECT " + updateType.trim()
				+ " FROM Customers WHERE id = (SELECT customer_id FROM Accounts WHERE account_number = ?)";
		final String updateQuery = "UPDATE Customers SET " + updateType.trim()
				+ " = ? WHERE id = (SELECT customer_id FROM accounts WHERE account_number = ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement checkStatement = conn.prepareStatement(getQuery)) {
			conn.setAutoCommit(false);

			checkStatement.setString(1, accountNumber);

			try (ResultSet rs = checkStatement.executeQuery()) {
				if (rs.next()) {
					final String existingValue = rs.getString(1);

					if (Objects.equals(existingValue, updateData)) {
						throw new DuplicateUpdateException("New value is the same as the existing value. No update needed.");
					}
				}
			}

			try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
				if (updateType.equalsIgnoreCase("dob")) {
					final LocalDate localDate = LocalDate.parse(updateData, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
					updateStatement.setDate(1, java.sql.Date.valueOf(localDate));
				} else {
					updateStatement.setString(1, updateData);
				}

				updateStatement.setString(2, accountNumber);

				if (!updateStatement.execute()) {
					conn.commit();
					return true;
				} else {
					conn.rollback();
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
			throw new DatabaseException("Database error : " + e.getMessage(), e);
		}
		return false;
	} 

	public static final CustomerDaoDatabaseImpl getInstance() {
		if (instance == null) {
			instance = new CustomerDaoDatabaseImpl();
		}
		return instance;
	}

}
