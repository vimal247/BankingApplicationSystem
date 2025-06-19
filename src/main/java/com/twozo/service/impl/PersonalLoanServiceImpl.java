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
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;
import com.twozo.service.LoanTypeService;
import com.twozo.validation.LoanValidator;
import com.twozo.validation.PersonalLoanValidator;

public class PersonalLoanServiceImpl implements LoanTypeService {
	
	private static int uniqueNumber = 1001;
	private static PersonalLoanServiceImpl instance;
	private static final LoanValidator<PersonalLoan> LOAN_VALIDATOR = PersonalLoanValidator.getInstance();
	private static final LoanDao LOAN_DAO = LoanDaoDatabaseImpl.getInstance();

	@Override
	public boolean applyLoan(final Loan loan) throws LoanNotEligibleException, InvalidLoanAmountException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException {
		if (loan != null) {
			final String loanId = generatePersonalLoanId();
			
			loan.setApplicationDate(LocalDate.now());
			loan.setLoanNumber(loanId);
			PersonalLoan personalLoan = (PersonalLoan) loan;
			loan.setInterestRate(getInterestRate(personalLoan));

			if (LOAN_VALIDATOR.validate(personalLoan)) {
				loan.setLoanStatus(LoanStatus.PENDING);
				
				if (!LOAN_DAO.isLoanNumberPresentByLoanType(personalLoan.getAccountNumber(), personalLoan.getLoanType())) {
					loan.setLoanStatus(LoanStatus.APPROVED);
					return LOAN_DAO.saveLoanById(loanId, personalLoan);
				}
				throw new DuplicateLoanException("Personal loan is already exist.");
			}
		}
		loan.setLoanStatus(LoanStatus.APPROVED);
		return false;
	}

	private double getInterestRate(final PersonalLoan personalLoan) throws InvalidLoanAmountException {
		final double loanAmount = personalLoan.getLoanAmount();

		if (loanAmount <= 50000) {
			return 1;
		} else if (loanAmount <= 100000) {
			return 2.5;
		} else if (loanAmount <= 500000) {
			return 3.5;
		} else if (loanAmount <= 1000000) {
			return 5;
		} else {
			throw new InvalidLoanAmountException("Invalid loan amount for Home Loan.");
		}
	}

	public static final PersonalLoanServiceImpl getInstance() {
		if (instance == null) {
			instance = new PersonalLoanServiceImpl();
		}
		return instance;
	}

	private String generatePersonalLoanId() {
		return "PLOAN" + getUniqueNumber();
	}

	private int getUniqueNumber() {
		return uniqueNumber++;
	}
}
