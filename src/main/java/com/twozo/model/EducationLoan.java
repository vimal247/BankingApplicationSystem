package com.twozo.model;

import com.twozo.enums.CourseDuration;
import com.twozo.enums.EducationLevel;
import com.twozo.enums.LoanTenure;
import com.twozo.enums.LoanType;

public class EducationLoan extends Loan {

	private EducationLevel educationLevel;
	private String institutionName;
	private String courseName;
	private CourseDuration courseDuration;

	public EducationLoan(final String loanholdername, final String accountNumber, final double loanAmount,
			final double balanceAmount, final LoanType loanType, final LoanTenure loanTenure, final double interestRate, final Customer customer,
			final String bankStatementUrl, final EducationLevel educationLevel, final String institutionName, final String courseName,
			final CourseDuration courseDuration) {
		super(accountNumber, loanAmount, balanceAmount, loanType, loanTenure, interestRate, customer);
		this.educationLevel = educationLevel;
		this.institutionName = institutionName;
		this.courseName = courseName;
		this.courseDuration = courseDuration;
	}
	
	public EducationLoan() {
		
	}

	@Override
	public String toString() {
		return "EducationLoan [Loan Number : " + getLoanNumber() + " Loan Holder Name : " + getCustomer().getName()
				+ ", Loan Amount : " + getLoanAmount() + ", Balance Amount : " + getBalanceAmount()
				+ ", Interest Rate : " + getInterestRate() + ", Loan Type : " + getLoanType() 
				+ ", Loan Tenure : " + getLoanTenure() + "Emi Amount : " + getEmiAmount() + " Year"
				+ ", Education Level : " + educationLevel + ", Institution Name : " + institutionName
				+ ", Course Name : " + courseName + ", Course Duration : " + courseDuration 
				+ ", Application Date : " + getApplicationDate() + "]";
	}

	public EducationLevel getEducationLevel() {
		return educationLevel;
	}

	public void setEducationLevel(final EducationLevel educationLevel) {
		this.educationLevel = educationLevel;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(final String institutionName) {
		this.institutionName = institutionName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(final String courseName) {
		this.courseName = courseName;
	}

	public CourseDuration getCourseDuration() {
		return courseDuration;
	}

	public void setCourseDuration(final CourseDuration courseDuration) {
		this.courseDuration = courseDuration;
	}

}
