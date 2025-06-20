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
import com.twozo.utils.EducationLoanInterestRateGenerator;
import com.twozo.utils.LoanNumberGenerator;
import com.twozo.validation.LoanValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EducationLoanServiceImpl implements LoanTypeService {

	private final LoanDao loanDao;

	private final LoanValidator<EducationLoan> loanValidator;

	@Autowired
	public  EducationLoanServiceImpl(final LoanDaoDatabaseImpl loanDao, final LoanValidator<EducationLoan> loanValidator){
		this.loanDao = loanDao;
		this.loanValidator = loanValidator;
	}
	@Override
	public boolean applyLoan(Loan loan) throws LoanNotEligibleException, InvalidLoanAmountException,
			LoanNotFoundException, DatabaseException, DuplicateLoanException {
		if (loan != null) {
			final String loanNumber = LoanNumberGenerator.getInstance().generateLoanNumber(loan.getLoanType());
			loan.setApplicationDate(LocalDate.now());
			loan.setLoanNumber(loanNumber);
			EducationLoan educationLoan = (EducationLoan) loan;
			loan.setInterestRate(EducationLoanInterestRateGenerator.getInstance().getInterestRate(loan.getLoanAmount()));

			if (loanValidator.validate(educationLoan)) {
				loan.setLoanStatus(LoanStatus.PENDING);

				if (!loanDao.isLoanNumberPresentByLoanType(educationLoan.getAccountNumber(), educationLoan.getLoanType())) {
					loan.setLoanStatus(LoanStatus.APPROVED);
					return loanDao.saveLoanById(loanNumber, educationLoan);
				}
				throw new DuplicateLoanException("Education loan is already exist.");
			}
		}
		return false;
	}
}
