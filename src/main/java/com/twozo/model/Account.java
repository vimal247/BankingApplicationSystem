package com.twozo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.twozo.enums.AccountStatus;
import com.twozo.enums.AccountType;
import com.twozo.enums.BankName;
import com.twozo.enums.BranchCode;
import com.twozo.enums.BranchName;

public final class Account {
	
	private static final BankName BANK_NAME = BankName.IOB;

	private long id;
	private String accountNumber;
	private String ifscCode;
	private double balance;
	private BranchName branchName;
	private AccountType accountType;
	private AccountStatus accountStatus;
	private BranchCode branchCode;

	private List<Transaction> transactions;

	private Customer customer;

	private Account(final String accountNumber, final String ifscCode, final BranchName branchName,
					final AccountType accountType, final double balance, final AccountStatus accountStatus, final BranchCode branchCode,
					final Customer customer) {
		this.accountNumber = accountNumber;
		this.ifscCode = ifscCode;
		this.branchName = branchName;
		this.accountType = accountType;
		this.balance = balance;
		this.accountStatus = accountStatus;
		this.branchCode = branchCode;
		this.customer = customer;
		transactions = new ArrayList<>();
	}

	public Account(final AccountType accountType, final double balance, final Customer customer) {
		this(null, null, null, accountType, balance, null, null, customer);
	}
	
	@Override
	public final int hashCode() {
		return Objects.hash(accountNumber);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return Objects.equals(accountNumber, other.accountNumber);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(final String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(final String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public BranchName getBranchName() {
		return branchName;
	}

	public void setBranchName(final BranchName branchName) {
		this.branchName = branchName;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(final AccountType accountType) {
		this.accountType = accountType;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(final double balance) {
		this.balance = balance;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(final AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	public BranchCode getBranchCode() {
		return branchCode;
	}

	public void setBranchCode(final BranchCode branchCode) { this.branchCode = branchCode; }

	public static BankName getBankName() {
		return BANK_NAME;
	}

	public List<Transaction> getTransaction() {
		return transactions;
	}

	public void setTransaction(final List<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public Account() {
		
	}
}
