package com.twozo.dao.impl.collection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.twozo.dao.AccountDao;
import com.twozo.dao.LoanDao;
import com.twozo.enums.AccountType;
import com.twozo.enums.LoanType;
import com.twozo.exception.account.AccountNotFoundException;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LoanDaoCollectionImpl implements LoanDao {	
	
	private final AccountDao accountDao;
	
	private final static Map<String, Loan> LOAN_MAP = new HashMap<>();
	private final static Map<LoanType, Map<String, Loan>> LOANS_BY_TYPE = new HashMap<>();

	@Autowired
	public LoanDaoCollectionImpl(final AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	/* ----- Loan Services ----- */
	@Override
	public boolean saveLoanById(final String loanNumber, final Loan loan) {
		if (loan != null) {
			LOAN_MAP.put(loanNumber, loan);
			LOANS_BY_TYPE.put(loan.getLoanType(), LOAN_MAP);
		
			return true;
		}
		return false;
	}

	@Override
	public final boolean repayLoanAmount(final String loanNumber, final double loanBalance) {
		final Loan loan = LOAN_MAP.get(loanNumber);

		if (loan != null) {
			loan.setBalanceAmount(loanBalance);
			return true;
		}

		return false;
	}

	@Override
	public final boolean loanSanction(final String accountNumber, final double amount) throws DatabaseException, AccountNotFoundException {
		final Account account = accountDao.getAccountByNumber(accountNumber);

		if (account != null) {
			account.setBalance(amount);
			return true;
		}
		return false;
	}

	@Override
	public final boolean removeLoan(final String loanNumber) {
		LOAN_MAP.remove(loanNumber);
		return true;
	}

	@Override
	public final Loan getLoanDetails(final String loanNumber) {
		return LOAN_MAP.get(loanNumber);
	}

	@Override
	public boolean updateLoanDetails(final String updateData, final String loanId, final String updateType)
			throws LoanProcessingException{
		final Loan loanDetails = LOAN_MAP.get(loanId);
		return updateDetail(updateData, loanDetails.getAccountNumber(), updateType);
	}

	@Override
	public final boolean isLoanNumberPresent(final String loanNumber) throws LoanNotFoundException, DatabaseException {
		if (LOAN_MAP.containsKey(loanNumber)) {
			return true;
		}
		throw new LoanNotFoundException("Loan Is Not Present");
	}

	@Override
	public final boolean isUniqueMobileForLoan(final String mobileNumber, final Loan loan)
			throws DuplicateEntryException {
		Map<String, Loan> loansOfSameType = LOANS_BY_TYPE.get(loan.getLoanType());

		if (loansOfSameType != null) {
			for (Loan existingLoan : loansOfSameType.values()) {
				if (existingLoan.getCustomer().getMobileNumber().trim().equalsIgnoreCase(mobileNumber)) {
					throw new DuplicateEntryException("Mobile number already exists for this loan type.");
				}
			}
		}
		return true;
	}

	@Override
	public final boolean isUniqueAadharForLoan(final String aadharNumber, final Loan loan)
			throws DuplicateEntryException {
		Map<String, Loan> loansOfSameType = LOANS_BY_TYPE.get(loan.getLoanType());

		if (loansOfSameType != null) {
			for (Loan existingLoan : loansOfSameType.values()) {
				if (existingLoan.getCustomer().getAadharNumber().equalsIgnoreCase(aadharNumber)) {
					throw new DuplicateEntryException("Aadhar Number is Already Exist.");
				}
			}
		}
		return true;
	}

	private boolean updateDetail(final String updateData, final String identity, final String updateType)
			throws LoanProcessingException {

		if (updateType.equalsIgnoreCase("name")) {
			return updateCustomerName(updateData, identity);
		}
		if (updateType.equalsIgnoreCase("mobileNo")) {
			return updateCustomerMobileNumber(updateData, identity);
		}
		if (updateType.equalsIgnoreCase("dob")) {
			return updateCustomerDob(updateData, identity);
		}
		if (updateType.equalsIgnoreCase("address")) {
			return updateCustomerAddress(updateData, identity);
		}
		return false;
	}

	private boolean updateCustomerName(final String name, final String accountNumber) throws LoanProcessingException {
		try {
			final Customer customer = accountDao.getAccountByNumber(accountNumber).getCustomer();
			
			if (customer != null) {
				customer.setName(name);
				return true;
			}
			return false;
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	// Update customer mobile number in storage, after checking for uniqueness.
	private boolean updateCustomerMobileNumber(final String mobileNumber, final String accountNumber)
			throws LoanProcessingException {
		try {
			final Account account = accountDao.getAccountByNumber(accountNumber);
			final AccountType accountType = account.getAccountType();

            if (accountDao.isUniqueMobileForAccount(mobileNumber, accountType)) {
                final Customer customer = accountDao.getAccountByNumber(accountNumber).getCustomer();
                customer.setMobileNumber(mobileNumber);

                return true;
            }
        } catch (DuplicateEntryException | DatabaseException | AccountNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
		return false;
	}

	// Update customer DOB in Storage using updateCustomerDob() method.
	private boolean updateCustomerDob(final String dob, final String accountNumber) throws LoanProcessingException {
		Customer customer;
		try {
			customer = accountDao.getAccountByNumber(accountNumber).getCustomer();

			if (customer != null) {
				final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				final LocalDate dobDate = LocalDate.parse(dob, formatter);
				customer.setDob(dobDate);
				
				return true;
			}
			return false;
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}

	// Update customer address in Storage using updateCustomerAddress() method.
	private boolean updateCustomerAddress(final String address, final String accountNumber)
			throws LoanProcessingException {
		Customer customer;
		try {
			customer = accountDao.getAccountByNumber(accountNumber).getCustomer();

			if (customer != null) {
				customer.setAddress(address);
				return true;
			}
			return false;
		} catch (DatabaseException | AccountNotFoundException e) {
			throw new LoanProcessingException(e.getMessage(), e);
		}
	}
	
	@Override
	public final boolean isLoanNumberPresentByLoanType(final String loanNumber, final LoanType loanType)
			throws LoanNotFoundException, DatabaseException {	
		@SuppressWarnings("unchecked")	
		List<Loan> loans =  (List<Loan>) LOANS_BY_TYPE.get(loanType); 
 
		if (loans == null || loans.isEmpty()) {	
			return false;
		}
		for (Loan loan : loans) {
			if (loan.getLoanNumber().equals(loanNumber)) {
				return true;
			}
		}
		return false;
	}
}
