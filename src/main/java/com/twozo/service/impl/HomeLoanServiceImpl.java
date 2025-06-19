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
import com.twozo.model.HomeLoan;
import com.twozo.model.Loan;
import com.twozo.service.LoanTypeService;
import com.twozo.validation.HomeLoanValidator;
import com.twozo.validation.LoanValidator;

public class HomeLoanServiceImpl implements LoanTypeService {
	
	private static int uniqueNumber = 1001;
	private static HomeLoanServiceImpl instance;
	private static final LoanValidator<HomeLoan> LOAN_VALIDATOR = HomeLoanValidator.getInstance();
	private static final LoanDao LOAN_DAO = LoanDaoDatabaseImpl.getInstance();
	
	@Override  
	public <T extends Loan> boolean applyLoan(T loan) throws InvalidLoanAmountException, LoanNotEligibleException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException {
		if (loan != null) {
			final String loanId = generateHomeLoanId();
			
			loan.setApplicationDate(LocalDate.now());
			loan.setLoanNumber(loanId);
			HomeLoan homeLoan = (HomeLoan) loan;
			loan.setInterestRate(getInterestRate(homeLoan)); 

			if (LOAN_VALIDATOR.validate(homeLoan)) {
				loan.setLoanStatus(LoanStatus.PENDING);
				
				if (!LOAN_DAO.isLoanNumberPresentByLoanType(homeLoan.getAccountNumber(), homeLoan.getLoanType())) {
					loan.setLoanStatus(LoanStatus.APPROVED);	
					
					return LOAN_DAO.saveLoanById(loanId, homeLoan);
				}
				throw new DuplicateLoanException("Home loan is already exist.");
			}
		}
		loan.setLoanStatus(LoanStatus.REJECTED);
		return false;
	}

	private double getInterestRate(final HomeLoan homeLoan) throws InvalidLoanAmountException {
		final double loanAmount = homeLoan.getLoanAmount();

		if (loanAmount <= 50000) {
			return 2;
		} else if (loanAmount <= 100000) {
			return 3;
		} else if (loanAmount <= 500000) {
			return 5;
		} else if (loanAmount <= 1000000) {
			return 7;
		} else {
			throw new InvalidLoanAmountException("Invalid loan amount for Home Loan.");
		}
	}

	public static final HomeLoanServiceImpl getInstance() {
		if (instance == null) {
			instance = new HomeLoanServiceImpl();
		}
		return instance;
	}

	private String generateHomeLoanId() {
		return "HLOAN" + getUniqueNumber();
	}

	private int getUniqueNumber() {
		return uniqueNumber++;
	}
}
