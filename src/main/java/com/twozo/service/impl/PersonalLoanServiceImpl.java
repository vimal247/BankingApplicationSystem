package com.twozo.service.impl;

import java.time.LocalDate;

import com.twozo.dao.LoanDao;
import com.twozo.enums.LoanStatus;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.loan.DuplicateLoanException;
import com.twozo.exception.loan.InvalidLoanAmountException;
import com.twozo.exception.loan.LoanNotEligibleException;
import com.twozo.exception.loan.LoanNotFoundException;
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;
import com.twozo.service.LoanTypeService;
import com.twozo.utils.LoanNumberGenerator;
import com.twozo.utils.PersonalLoanInterestRateGenerator;
import com.twozo.validation.LoanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalLoanServiceImpl implements LoanTypeService {

	private final LoanDao loanDao;
	private final LoanValidator<PersonalLoan> loanValidator;

	@Autowired
	public PersonalLoanServiceImpl(final LoanDao loanDao, final LoanValidator<PersonalLoan> loanValidator) {
		this.loanDao = loanDao;
		this.loanValidator = loanValidator;
	}

	@Override
	public boolean applyLoan(final Loan loan) throws LoanNotEligibleException, InvalidLoanAmountException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException {
		if (loan != null) {
			final String loanNumber = LoanNumberGenerator.getInstance().generateLoanNumber(loan.getLoanType());
			
			loan.setApplicationDate(LocalDate.now());
			loan.setLoanNumber(loanNumber);
			PersonalLoan personalLoan = (PersonalLoan) loan;
			loan.setInterestRate(PersonalLoanInterestRateGenerator.getINSTANCE().getInterestRate(loan.getLoanAmount()));

			if (loanValidator.validate(personalLoan)) {
				loan.setLoanStatus(LoanStatus.PENDING);
				
				if (!loanDao.isLoanNumberPresentByLoanType(personalLoan.getAccountNumber(), personalLoan.getLoanType())) {
					loan.setLoanStatus(LoanStatus.APPROVED);
					return loanDao.saveLoanById(loanNumber, personalLoan);
				}
				throw new DuplicateLoanException("Personal loan is already exist.");
			}
		}
		return false;
	}
}
