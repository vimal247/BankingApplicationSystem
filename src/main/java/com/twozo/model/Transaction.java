package com.twozo.model;

import java.time.LocalDateTime;

import com.twozo.enums.TransactionType;

public class Transaction {

	private long transactionId;
	private String accountNumber;
	private double amount;
	private LocalDateTime dateTime;
	private TransactionType transactionType;
	private String transferedAccountNumber;
	private long transferedAccountId;
	private long accountId;

	public Transaction(final long transactionId, final String accountNumber, final double amount,
			final LocalDateTime dateTime, final TransactionType transactionType, final String transferedAccountNumber) {
		this.transactionId = transactionId;
		this.accountNumber = accountNumber;
		this.amount = amount;
		this.dateTime = dateTime;
		this.transactionType = transactionType;
		this.transferedAccountNumber = transferedAccountNumber;
	}

	@Override
	public String toString() {
		return "Transaction History = [Account Number = " + accountNumber + ", Amount = " + amount + ", Date & Time = "
				+ dateTime + ", Transaction Type = " + transactionType + " , Transfered Account Number = " + transferedAccountNumber + "]";
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(final String accountNo) {
		this.accountNumber = accountNo;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(final double amount) {
		this.amount = amount;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(final LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(final TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final long transactionId) {
		this.transactionId = transactionId;
	}
	
	public String getTransferedAccountNumber() {
		return transferedAccountNumber;
	}

	public void setTransferedAccountNumber(final String transferedAccountNumber) {
		this.transferedAccountNumber = transferedAccountNumber;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public long getTransferedAccountId() {
		return transferedAccountId;
	}

	public void setTransferedAccountId(long transferedAccountId) {
		this.transferedAccountId = transferedAccountId;
	}

	public Transaction() {

	}
}
