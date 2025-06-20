package com.twozo.dao.impl.database;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.twozo.dao.LoanDao;
import com.twozo.enums.CourseDuration;
import com.twozo.enums.EducationLevel;
import com.twozo.enums.EducationalLoanColumn;
import com.twozo.enums.EmploymentType;
import com.twozo.enums.HomeLoanColumn;
import com.twozo.enums.LoanType;
import com.twozo.enums.PersonalLoanColumn;
import com.twozo.enums.PropertyType;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.model.EducationLoan;
import com.twozo.model.HomeLoan;
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;
import com.twozo.database.DatabaseConnection;
import com.twozo.repository.EducationLoanRepository;
import com.twozo.repository.HomeLoanRepository;
import com.twozo.repository.LoanRepository;
import com.twozo.repository.PersonalLoanRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanDaoDatabaseImpl implements LoanDao {

	private LoanRepository loanRepository;
	private HomeLoanRepository homeLoanRepository;
	private PersonalLoanRepository personalLoanRepository;
	private EducationLoanRepository educationLoanRepository;

	@Autowired
	public LoanDaoDatabaseImpl(final HomeLoanRepository homeLoanRepository, final LoanRepository loanRepository,
							   final PersonalLoanRepository personalLoanRepository, final EducationLoanRepository educationLoanRepository){
		this.loanRepository = loanRepository;
		this.homeLoanRepository = homeLoanRepository;
		this.personalLoanRepository = personalLoanRepository;
		this.educationLoanRepository = educationLoanRepository;
	}

	@Override
	public boolean saveLoanById(final String loanId, final Loan loan)
			throws DatabaseException {
		if (loan == null) {
			return false;
		}
		// Base query for all loan types
		final String baseQuery = "WITH account_cte AS (SELECT id FROM accounts WHERE account_number = ?) "
				+ "INSERT INTO loans (account_id, loan_number, loan_amount, balance_amount, interest_rate, "
				+ "loan_type, loan_tenure, emi_amount, loan_status, application_date) "
				+ "SELECT account_cte.id, ?, ?, ?, ?, ?, ?, ?, ?, ? FROM account_cte RETURNING id";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement baseStatement = conn.prepareStatement(baseQuery)) {
			conn.setAutoCommit(false);
			int countOne = 1;
			int countTwo = 1;
			// Set values for common loan fields
			baseStatement.setString(countOne++, loan.getAccountNumber());
			baseStatement.setString(countOne++, loan.getLoanNumber());
			baseStatement.setDouble(countOne++, loan.getLoanAmount());
			baseStatement.setDouble(countOne++, loan.getBalanceAmount());
			baseStatement.setDouble(countOne++, loan.getInterestRate());
			baseStatement.setObject(countOne++, loan.getLoanType(), Types.OTHER);
			baseStatement.setObject(countOne++, loan.getLoanTenure(), Types.OTHER);
			baseStatement.setDouble(countOne++, loan.getEmiAmount());
			baseStatement.setObject(countOne++, loan.getLoanStatus(), Types.OTHER);
			baseStatement.setDate(countOne, java.sql.Date.valueOf(loan.getApplicationDate()));

			long generatedLoanId;
			try (ResultSet rs = baseStatement.executeQuery()) {
				if (rs.next()) {
					generatedLoanId = rs.getLong("id");
				} else {
					conn.rollback();
					return false;
				}
			}

			String loanTypeQuery;
			if (loan instanceof HomeLoan) {
				loanTypeQuery = "INSERT INTO home_loans (loan_id, income, employment_type, property_address, property_type) VALUES (?, ?, ?, ?, ?)";

				try (PreparedStatement loanTypeStatement = conn.prepareStatement(loanTypeQuery)) {
					final HomeLoan homeLoan = (HomeLoan) loan;

					loanTypeStatement.setLong(countTwo++, generatedLoanId);
					loanTypeStatement.setDouble(countTwo++, homeLoan.getIncome());
					loanTypeStatement.setObject(countTwo++, homeLoan.getEmployementType(), Types.OTHER);
					loanTypeStatement.setString(countTwo++, homeLoan.getPropertyAddress());
					loanTypeStatement.setObject(countTwo, homeLoan.getPropertyType(), Types.OTHER);

					if (!loanTypeStatement.execute()) {
						conn.rollback();
						return false;
					}
				}
			} else if (loan instanceof PersonalLoan) {
				loanTypeQuery = "INSERT INTO personal_loans (loan_id, income, employment_type, bank_statement_url) VALUES (?, ?, ?, ?)";

				try (PreparedStatement loanTypeStatement = conn.prepareStatement(loanTypeQuery)) {
					final PersonalLoan personalLoan = (PersonalLoan) loan;

					loanTypeStatement.setString(countTwo++, loanId);
					loanTypeStatement.setDouble(countTwo++, personalLoan.getIncome());
					loanTypeStatement.setObject(countTwo, personalLoan.getEmploymentType(), Types.OTHER);

					if (!loanTypeStatement.execute()) {
						conn.rollback();
						return false;
					}
				}
			} else if (loan instanceof EducationLoan) {
				loanTypeQuery = "INSERT INTO education_loans (loan_id, bank_statement_url, education_level, institution_name, course_name, course_duration) VALUES (?, ?, ?, ?, ?, ?)";

				try (PreparedStatement loanTypeStatement = conn.prepareStatement(loanTypeQuery)) {
					final EducationLoan educationLoan = (EducationLoan) loan;

					loanTypeStatement.setString(countTwo++, loanId);
					loanTypeStatement.setObject(countTwo++, educationLoan.getEducationLevel(), Types.OTHER);
					loanTypeStatement.setString(countTwo++, educationLoan.getInstitutionName());
					loanTypeStatement.setString(countTwo++, educationLoan.getCourseName());
					loanTypeStatement.setObject(countTwo, educationLoan.getCourseDuration(), Types.OTHER);

					if (!loanTypeStatement.execute()) {
						conn.rollback();
						return false;
					}
				}
			}
			conn.commit();
			return true;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	@Override
	public final boolean isLoanNumberPresent(final String loanNumber) throws DatabaseException {
		final String query = "SELECT * FROM loans WHERE loan_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, loanNumber);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final boolean loanSanction(final String accountNumber, final double balance)
			throws DatabaseException {
		final String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
			conn.setAutoCommit(false);

			updateStatement.setDouble(1, balance);
			updateStatement.setString(2, accountNumber);

			if (!updateStatement.execute()) {
				conn.commit();
				return true;
			}
			conn.rollback();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final Loan getLoanDetails(final String loanNumber) throws LoanNotFoundException {
		Loan loan = loanRepository.findByLoanNumber(loanNumber);

		if (loan == null) {
			throw new LoanNotFoundException("Loan not found for loan number: " + loanNumber);
		}

		final LoanType loanType = loan.getLoanType();

		switch (loanType.getDescription()) {
			case "homeloan":
				loan = homeLoanRepository.findById(loan.getId()).orElse(new HomeLoan());
				break;
			case "personalloan":
				loan = personalLoanRepository.findById(loan.getId()).orElse(new PersonalLoan());
				break;
			case "educationloan":
				loan = educationLoanRepository.findById(loan.getId()).orElse(new EducationLoan());
				break;
			default:
				break;
		}

		return loan;
	}

	private void fetchHomeLoanDetails(final Connection conn, final HomeLoan loan, final long loanId)
			throws SQLException {
		final String query = "SELECT income, employment_type, property_address,"
				+ " property_type FROM home_loans WHERE loan_id = ?";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, loanId);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					loan.setIncome(rs.getDouble(HomeLoanColumn.INCOME.getDescription()));
					loan.setEmployementType(EmploymentType.valueOf(rs.getString(HomeLoanColumn.EMPLOYMENT_TYPE.getDescription())));
					loan.setPropertyAddress(rs.getString(HomeLoanColumn.PROPERTY_ADDRESS.getDescription()));
					loan.setPropertyType(PropertyType.valueOf(rs.getString(HomeLoanColumn.PROPERTY_TYPE.getDescription())));
				}
			}
		}
	}

	private void fetchPersonalLoanDetails(final Connection conn, final PersonalLoan loan, final long loanId)
			throws SQLException {
		final String query = "SELECT income, employment_type, FROM personal_loan WHERE loan_id = ?";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, loanId);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					loan.setIncome(rs.getDouble(PersonalLoanColumn.INCOME.hashCode()));
					loan.setEmploymentType(EmploymentType.valueOf(rs.getString(PersonalLoanColumn.EMPLOYMENT_TYPE.hashCode())));
				}
			}
		}
	}

	private void fetchEducationLoanDetails(final Connection conn, final EducationLoan loan, final long loanId)
			throws SQLException {
		final String query = "SELECT education_level, institution_name, "
				+ "course_name, course_duration FROM education_loan WHERE loan_id = ?";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setLong(1, loanId);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next()) {
					loan.setEducationLevel(EducationLevel.valueOf(rs.getString(EducationalLoanColumn.EDUCATIONAL_LEVEL.getDescription())));
					loan.setInstitutionName(rs.getString(EducationalLoanColumn.INSTITUTION_NAME.getDescription()));
					loan.setCourseName(rs.getString(EducationalLoanColumn.COURSE_NAME.getDescription()));
					loan.setCourseDuration(CourseDuration.valueOf(rs.getString(EducationalLoanColumn.COURSE_DURATION.getDescription())));
				}
			}
		}
	}

	@Override
	public final boolean removeLoan(final String loanNumber) throws LoanNotFoundException {
		final Loan loan = loanRepository.findByLoanNumber(loanNumber);

		if (loan == null) {
			throw new LoanNotFoundException("Loan not found for loan number: " + loanNumber);
		}

		loanRepository.delete(loan);
		return true;
	}

	@Override
	public final boolean repayLoanAmount(final String loanNumber, final double repaymentAmount)
            throws LoanNotFoundException {
		final Loan loan = loanRepository.findByLoanNumber(loanNumber);

		if (loan == null) {
			throw new LoanNotFoundException("Loan not found for loan number: " + loanNumber);
		}

		loan.setBalanceAmount(loan.getBalanceAmount() - repaymentAmount);
		return true;
	}

	@Override
	public boolean updateLoanDetails(final String updateData, final String loanId, final String updateType)
            throws DuplicateUpdateException, DatabaseException, InvalidCustomerUpdateException, LoanNotFoundException {
		final Loan loan = getLoanDetails(loanId);

		if (loan != null) {
			return updateDetail(updateData, loan.getAccountNumber(), updateType);
		}
		return false;
	}

	@Override
	public final boolean isUniqueMobileForLoan(final String mobileNumber, final Loan loan)
			throws DuplicateEntryException, DatabaseException {
		final String query = "SELECT COUNT(*) FROM loan WHERE mobile_number = ? AND loan_type = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, mobileNumber);
			statement.setObject(2, loan.getLoanType());

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					throw new DuplicateEntryException("mobile number already exist in loan type.");
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return true;
	}

	@Override
	public final boolean isUniqueAadharForLoan(final String aadharNumber, final Loan loan)
			throws DuplicateEntryException, DatabaseException {
		final String query = "SELECT COUNT(*) FROM loan WHERE aadhar_number = ? AND loan_type = ?";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, aadharNumber);
			statement.setObject(2, loan.getLoanType(), Types.OTHER);

			try (ResultSet rs = statement.executeQuery()) {
				if (rs.next() && rs.getInt(1) > 0) {
					throw new DuplicateEntryException("Aadhar number already exist in loan type.");
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return true;
	}

	private boolean updateDetail(final String updateData, final String accountNumber, final String updateType)
			throws InvalidCustomerUpdateException, DatabaseException, DuplicateUpdateException {
		if (!isValidKey(updateType)) {
			throw new InvalidCustomerUpdateException(updateType);
		}
		String getQuery;
		String updateQuery;

		getQuery = "SELECT " + updateType.trim()
				+ " FROM customers WHERE id = (SELECT customer_id FROM accounts WHERE account_number = ?)";
		updateQuery = "UPDATE customers SET " + updateType.trim()
				+ " = ? WHERE id = (SELECT customer_id FROM accounts WHERE account_number = ?)";

		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement checkStatement = conn.prepareStatement(getQuery)) {
			conn.setAutoCommit(false);

			checkStatement.setString(1, accountNumber);

			try (ResultSet rs = checkStatement.executeQuery()) {
				if (rs.next()) {
					final String existingValue = rs.getString(1);

					if (existingValue.equals(updateData)) {
						throw new DuplicateUpdateException("Update data is  existing.");
					}
				}
			}

			try (PreparedStatement updatesStatement = conn.prepareStatement(updateQuery)) {
				if (updateType.equalsIgnoreCase("dob")) {
					final LocalDate localDate = LocalDate.parse(updateData, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

					updatesStatement.setDate(1, java.sql.Date.valueOf(localDate));
				} else {
					updatesStatement.setString(1, updateData);
				}
				updatesStatement.setString(2, accountNumber);

				if (!updatesStatement.execute()) {
					conn.commit();
					return true;
				} else {
					conn.rollback();
				}
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public final boolean isLoanNumberPresentByLoanType(final String accountNumber, final LoanType loanType)
			throws DatabaseException {
		final String query = "SELECT 1 FROM loans WHERE account_id = (SELECT id FROM Accounts WHERE account_number = ?) AND loan_type = ?";

		try (final Connection conn = DatabaseConnection.getConnection();
				final PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, accountNumber);
			statement.setObject(2, loanType, Types.OTHER);

			try (final ResultSet rs = statement.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	private boolean isValidKey(String key) {
		return key.equalsIgnoreCase("name") || key.equalsIgnoreCase("mobile_number") || key.equalsIgnoreCase("dob")
				|| key.equalsIgnoreCase("address");
	}

	private LoanDaoDatabaseImpl() {

	}
}