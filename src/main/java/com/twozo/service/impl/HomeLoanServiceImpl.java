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
import com.twozo.utils.HomeLoanInterestRateGenerator;
import com.twozo.utils.LoanNumberGenerator;
import com.twozo.validation.LoanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeLoanServiceImpl implements LoanTypeService {

	private final LoanDao loanDao;

	private final LoanValidator<HomeLoan> loanValidator;

	@Autowired
	public HomeLoanServiceImpl(final LoanDaoDatabaseImpl loanDao, final LoanValidator<HomeLoan> loanValidator) {
		this.loanDao = loanDao;
		this.loanValidator = loanValidator;
	}

	@Override  
	public <T extends Loan> boolean applyLoan(T loan) throws InvalidLoanAmountException, LoanNotEligibleException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException {
		if (loan != null) {
			final String loanNumber = LoanNumberGenerator.getInstance().generateLoanNumber(loan.getLoanType());
			
			loan.setApplicationDate(LocalDate.now());
			loan.setLoanNumber(loanNumber);
			HomeLoan homeLoan = (HomeLoan) loan;
			loan.setInterestRate(HomeLoanInterestRateGenerator.getInstance().getInterestRate(loan.getLoanAmount()));

			if (loanValidator.validate(homeLoan)) {
				loan.setLoanStatus(LoanStatus.PENDING);
				
				if (!loanDao.isLoanNumberPresentByLoanType(homeLoan.getAccountNumber(), homeLoan.getLoanType())) {
					loan.setLoanStatus(LoanStatus.APPROVED);	
					
					return loanDao.saveLoanById(loanNumber, homeLoan);
				}
				throw new DuplicateLoanException("Home loan is already exist.");
			}
		}
		return false;
	}
}
