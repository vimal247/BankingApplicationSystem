package com.twozo.service.impl;

import java.time.LocalDate;

import com.twozo.dao.LoanDao;
import com.twozo.dao.impl.database.LoanDaoDatabaseImpl;
import com.twozo.enums.LoanStatus;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.DuplicateLoanException;
import com.twozo.exception.loan.InvalidLoanAmountException;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.model.EducationLoan;
import com.twozo.model.Loan;
import com.twozo.service.LoanTypeService;
import com.twozo.validation.EducationLoanValidator;
import com.twozo.validation.LoanValidator;

public class EducationLoanServiceImpl implements LoanTypeService {

	private static int uniqueNumber = 1001;
	private static EducationLoanServiceImpl instance;
	private static final LoanValidator<EducationLoan> LOAN_VALIDATOR = EducationLoanValidator.getInstance();
	private static final LoanDao LOAN_DAO = LoanDaoDatabaseImpl.getInstance();

	@Override
	public boolean applyLoan(Loan loan) throws LoanNotEligibleException, InvalidLoanAmountException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException {
		if (loan != null) {
			final String loanId = generateEducationLoanId();
			loan.setApplicationDate(LocalDate.now());
			loan.setLoanNumber(loanId);
			EducationLoan educationLoan = (EducationLoan) loan;
			loan.setInterestRate(getInterestRate(educationLoan));

			if (LOAN_VALIDATOR.validate(educationLoan)) {
				loan.setLoanStatus(LoanStatus.PENDING);

				if (!LOAN_DAO.isLoanNumberPresentByLoanType(educationLoan.getAccountNumber(), educationLoan.getLoanType())) {
					loan.setLoanStatus(LoanStatus.APPROVED);
					return LOAN_DAO.saveLoanById(loanId, educationLoan);	
				}
				throw new DuplicateLoanException("Education loan is already exist.");
			}
		}
		loan.setLoanStatus(LoanStatus.APPROVED);
		return false;
	}

	private double getInterestRate(final EducationLoan educationLoan) throws InvalidLoanAmountException {
		final double loanAmount = educationLoan.getLoanAmount();

		if (loanAmount <= 50000) {
			return 0.5;
		} else if (loanAmount <= 100000) {
			return 1;
		} else if (loanAmount <= 500000) {
			return 1.5;
		} else if (loanAmount <= 1000000) {
			return 2;
		} else {
			throw new InvalidLoanAmountException("Invalid loan amount for Education Loan");
		}
	}

	public static final EducationLoanServiceImpl getInstance() {
		if (instance == null) {
			instance = new EducationLoanServiceImpl();
		}
		return instance;
	}

	private String generateEducationLoanId() {
		return "ELOAN" + getUniqueNumber();
	}

	private int getUniqueNumber() {
		return uniqueNumber++;
	}
}
