package com.twozo.model;

import java.time.LocalDate;

import com.twozo.enums.LoanStatus;
import com.twozo.enums.LoanTenure;
import com.twozo.enums.LoanType;

public class Loan {

	private long id;
	private String loanNumber;
	private double loanAmount;
	private String accountNumber;
	private LoanType loanType;
	private LoanTenure loanTenure;
	private double balanceAmount;	
	private double emiAmount;
	private LoanStatus loanStatus;
	private LocalDate applicationDate;
	private double interestRate;
	private Customer customer;
	
	private Loan(final String loanNumber, final String accountNumber, final double loanAmount, final double balanceAmount, final LoanType loanType,
			final LoanTenure loanTenure, final LoanStatus loanStatus, final LocalDate applicationDate, final double interestRate, final Customer customer) {
		this.loanNumber = loanNumber;
		this.accountNumber = accountNumber;
		this.loanAmount = loanAmount;
		this.balanceAmount = balanceAmount;
		this.loanType = loanType;
		this.loanTenure = loanTenure;
		this.loanStatus = loanStatus;
		this.applicationDate = applicationDate;
		this.interestRate = interestRate;
		this.customer = customer;
	}
	
	public Loan(final String accountNumber,final double loanAmount, final double balanceAmount, final LoanType loanType, final LoanTenure loanTenure,
			final double interestRate, final Customer customer) {
		this(null, accountNumber, loanAmount, balanceAmount, loanType, loanTenure, null, null, interestRate, customer);
	}

	@Override
	public String toString() {
		return "Loan [loanNumber=" + loanNumber + ", loanAmount=" + loanAmount + ", accountNumber=" + accountNumber
				+ ", loanType=" + loanType + ", loanTenure=" + loanTenure + ", balanceAmount=" + balanceAmount
				+ ", emiAmount=" + emiAmount + ", loanStatus=" + loanStatus + ", applicationDate=" + applicationDate
				+ ", interestRate=" + interestRate + ", customer=" + customer + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLoanNumber() {
		return loanNumber;
	}

	public void setLoanNumber(final String loanNumber) {
		this.loanNumber = loanNumber;
	}

	public double getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(final double loanAmount) {
		this.loanAmount = loanAmount;
	}

	public LoanType getLoanType() {
		return loanType;
	}

	public void setLoanType(final LoanType loanType) {
		this.loanType = loanType;
	}

	public LoanTenure getLoanTenure() {
		return loanTenure;
	}

	public void setLoanTenure(final LoanTenure loanTenure) {
		this.loanTenure = loanTenure;
	}

	public LoanStatus getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(final LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}

	public LocalDate getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(final LocalDate localDate) {
		this.applicationDate = localDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(final double interestRate) {
		this.interestRate = interestRate;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(final String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(final double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public double getEmiAmount() {
		return emiAmount;
	}

	public void setEmiAmount(final double emiAmount) {
		this.emiAmount = emiAmount;
	}
	
	public Loan() {
		
	}
}
