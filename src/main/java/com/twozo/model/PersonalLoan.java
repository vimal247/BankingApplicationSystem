package com.twozo.model;

import com.twozo.enums.EmploymentType;
import com.twozo.enums.LoanTenure;
import com.twozo.enums.LoanType;

public class PersonalLoan extends Loan {

	private double income;
	private EmploymentType employmentType;

	public PersonalLoan(final String loanholdername, final String accountNumber, final double loanAmount,
			final double balanceAmount, final LoanType loanType, final LoanTenure loanTenure, final Customer customer, final double income,
			final EmploymentType employmentType, final double interestRate) {
		super(accountNumber, loanAmount, balanceAmount, loanType, loanTenure, 0, customer);
		this.income = income;
		this.employmentType = employmentType;
	}

	public PersonalLoan() {
		
	}
	
	@Override
	public String toString() {
		return "PersonalLoan [Loan Number : " + getLoanNumber() + ", Loan Holder Name : " + getCustomer().getName()
				+ ", Loan Amount : " + getLoanAmount() + ", Balance Amount : " + getBalanceAmount()
				+ ", Interest Rate : " + getInterestRate() + ", Loan Type : " + getLoanType() 
				+ ", Loan Tenure : " + getLoanTenure() + " Year" + ", Income : " + income
				+ ", Employement Type : " + employmentType
				+ ", Application Date : " + getApplicationDate() + "]";
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(final double income) {
		this.income = income;
	}

	public EmploymentType getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(final EmploymentType employmentType) {
		this.employmentType = employmentType;
	}
}
