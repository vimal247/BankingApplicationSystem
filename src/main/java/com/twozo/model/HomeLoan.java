package com.twozo.model;

import com.twozo.enums.EmploymentType;
import com.twozo.enums.LoanTenure;
import com.twozo.enums.LoanType;
import com.twozo.enums.PropertyType;

public class HomeLoan extends Loan {
	
	private double income;
	private EmploymentType employmentType;
	private String bankStatementUrl;
	private String propertyAddress;
	private PropertyType propertyType;

	public HomeLoan(final String loanholdername, final String accountNumber, final double loanAmount,
			final double balanceAmount, final LoanType loanType, final LoanTenure loanTenure, final Customer customer,
			final HomeLoan homeLoan, final double income, final EmploymentType employmentType, final double interestRate,
			final String bankStatementUrl, final String propertyAddress, final PropertyType propertyType) {
		super(accountNumber, loanAmount, balanceAmount, loanType, loanTenure, 0, customer);
		this.income = income;
		this.employmentType = employmentType;
		this.bankStatementUrl = bankStatementUrl;
		this.propertyAddress = propertyAddress;
		this.propertyType = propertyType;
	}
	
	public HomeLoan() {
		
	}

	@Override
	public String toString() {

		return "HomeLoan [Loan Number : " + getLoanNumber() + ", Loan Holder Name : " + getCustomer().getName()
				+ ", Loan Amount : " + getLoanAmount() + ", Balance Amount : " + getBalanceAmount() 
				+ ", Interest Rate : " + getInterestRate() + ", Loan Type : " + getLoanType() 
				+ ", Loan Tenure : " + getLoanTenure() + " Year" + ", Income : " + income
				+ ", Employement Type : " + employmentType + ", Bank Statement Url : " + bankStatementUrl
				+ ", Property Address : " + propertyAddress + ", Property Type : " + propertyType
				+ ", Application Date : " + getApplicationDate() + "]";
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(final double income) {
		this.income = income;
	}

	public EmploymentType getEmployementType() {
		return employmentType;
	}

	public void setEmployementType(final EmploymentType employmentType) {
		this.employmentType = employmentType;
	}

	public String getPropertyAddress() {
		return propertyAddress;
	}

	public void setPropertyAddress(final String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}

	public PropertyType getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(final PropertyType propertyType) {
		this.propertyType = propertyType;
	}

	public String getBankStatementUrl() {
		return bankStatementUrl;
	}

	public void setBankStatementUrl(final String bankStatementUrl) {
		this.bankStatementUrl = bankStatementUrl;
	}
}
