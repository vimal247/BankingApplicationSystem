package com.twozo.dao;

import com.twozo.enums.LoanType;
import com.twozo.exception.account.InsufficientAmount;
import com.twozo.exception.customer.DuplicateEntryException;
import com.twozo.exception.customer.DuplicateUpdateException;
import com.twozo.exception.customer.InvalidCustomerUpdateException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.model.Loan;

public interface LoanDao {
	/* ----- Loan Services ----- */
	boolean saveLoanById(final String loanId, final Loan loan) throws LoanNotEligibleException, DatabaseException;

	boolean removeLoan(final String loanNumber) throws DatabaseException;

	boolean loanSanction(final String accountNumber, final double amount) throws InsufficientAmount, DatabaseException;

	Loan getLoanDetails(final String loanId) throws DatabaseException;

	boolean repayLoanAmount(final String loanId, final double loanBalance) throws InsufficientAmount, DatabaseException;

	boolean updateLoanDetails(final String updateData, final String loanId, final String updateType)
			throws LoanProcessingException, DuplicateUpdateException, DatabaseException, InvalidCustomerUpdateException;

	boolean isLoanNumberPresent(final String loanNumber) throws LoanNotFoundException, DatabaseException;

	boolean isLoanNumberPresentByLoanType(final String accountNumber, final LoanType loanType) throws LoanNotFoundException, DatabaseException;
	
	boolean isUniqueMobileForLoan(final String mobileNumber, final Loan loan)
			throws DuplicateEntryException, DatabaseException;

	boolean isUniqueAadharForLoan(final String aadharNumber, final Loan loan)	
			throws DuplicateEntryException, DatabaseException;

}
