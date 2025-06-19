package com.twozo.dao.impl.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.twozo.dao.InquiryDao;
import com.twozo.exception.database.DatabaseException;
import com.twozo.util.DatabaseConnection;

public class InquiryDaoDatabaseImpl implements InquiryDao {
	private static InquiryDaoDatabaseImpl instance;

	/* ----- Inquiry Services ----- */
	@Override
	public final double getBalance(final String accountNumber) throws DatabaseException {
		final String query = "SELECT balance FROM accounts WHERE account_number = ? ";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query);) {
			statement.setString(1, accountNumber);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return rs.getDouble("balance");
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException("Database error : ", e);
		}
		return 0;
	}

	public static final InquiryDaoDatabaseImpl getInstance() {
		if (instance == null) {
			instance = new InquiryDaoDatabaseImpl();
		}
		return instance;
	}
}
