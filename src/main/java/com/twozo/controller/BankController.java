package com.twozo.controller;

import java.util.List;

import com.twozo.enums.AccountType;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.inquiry.InquiryProcessingException;
import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.exception.transaction.TransactionProcessingException;
import com.twozo.model.*;
import com.twozo.service.BankService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iob/")
public abstract class BankController {

	private final BankService BANK_SERVICE = getBankService();

	// Abstract method to be implemented by subclasses
	protected abstract BankService getBankService();

	/* ----- Account Services ----- */

	@PostMapping("account")
	public boolean createAccount(@RequestBody Account account) throws AccountProcessingException, CustomerProcessingException {
		return BANK_SERVICE.createAccount(account);
	}

    @GetMapping("account/{accountNumber}")
	public Account getAccountDetails(@PathVariable String accountNumber) throws AccountProcessingException {
		return BANK_SERVICE.getAccountDetails(accountNumber);
	}

	@PatchMapping("account/{accountNumber}")
	public boolean deactivateAccount(@PathVariable String accountNumber) throws AccountProcessingException {
		return BANK_SERVICE.deactivateAccount(accountNumber);
	}

	@PutMapping("account/{accountNumber}/type")
	public boolean updateAccountType(@PathVariable String accountNumber, @RequestParam AccountType accountType) throws AccountProcessingException {
		return BANK_SERVICE.updateAccountType(accountNumber, accountType);
	}

	@GetMapping("account/available/{accountNumber}")
	public final boolean isAccountPresentInBranch(@PathVariable String accountNumber) throws AccountProcessingException {
		return BANK_SERVICE.isAccountPresentInBranch(accountNumber);
	}

	@GetMapping("account/exists/{accountNumber}")
	public final boolean isAccountAvailable(@PathVariable String accountNumber) throws AccountProcessingException {
		return BANK_SERVICE.isAccountAvailable(accountNumber);
	}

	/* ----- Customer Services ----- */

	@GetMapping("customer/{accountNumber}")
	public Customer getCustomerDetails(@PathVariable String accountNumber) throws CustomerProcessingException {
		return BANK_SERVICE.getCustomerDetails(accountNumber);
	}

	@PatchMapping("customer/{accountNumber}")
	public boolean updateCustomerDetail(@RequestParam String updateData, @PathVariable String accountNumber,
										@RequestParam String updateType) throws CustomerProcessingException {
		return BANK_SERVICE.updateCustomerDetail(updateData, accountNumber, updateType);
	}

	/* ----- Inquiry Services ----- */

	@GetMapping("inquiry/{accountNumber}")
	public final double enquireBalance(@PathVariable String accountNumber) throws InquiryProcessingException {
		return BANK_SERVICE.getBalance(accountNumber);
	}

	/* ----- Transaction Services ----- */
	@PostMapping("transaction/deposit")
	public boolean depositAmount(@RequestParam String accountNumber, @RequestParam double amount) throws TransactionProcessingException {
		return BANK_SERVICE.depositAmount(accountNumber, amount);
	}

	@PostMapping("transaction/withdraw")
	public boolean withdrawAmount(@RequestParam String accountNumber, @RequestParam double money) throws TransactionProcessingException {
		return BANK_SERVICE.withdraw(accountNumber, money);
	}

	@PostMapping("transaction/transfer")
	public boolean transferFunds(@RequestParam String senderAccountNumber, @RequestParam double money,
								 @RequestParam String receiverAccountNumber) throws TransactionProcessingException {
		return BANK_SERVICE.transferFunds(senderAccountNumber, money, receiverAccountNumber);
	}

	@GetMapping("transaction/history/{accountNumber}")
	public List<Transaction> getTransactionHistory(@PathVariable String accountNumber) throws TransactionProcessingException {
		return BANK_SERVICE.getTransanctionHistory(accountNumber);
	}

	/* ----- Loan Services ----- */

	@PostMapping("loan/apply")
	public boolean applyLoan(@RequestBody Loan loan) throws LoanProcessingException {
		return BANK_SERVICE.applyloan(loan);
	}

	@GetMapping("loan/{loanNumber}")
	public Loan getLoanDetails(@PathVariable String loanNumber) throws LoanProcessingException {
		return BANK_SERVICE.getLoanDetails(loanNumber);
	}

	@GetMapping("loan/exist/{loanNumber}")
	public final boolean isLoanNumberPresent(@PathVariable String loanNumber) throws LoanProcessingException {
		return BANK_SERVICE.isLoanNumberPresent(loanNumber);
	}

	@PatchMapping("loan/{loanNumber}")
	public final boolean updateLoanDetails(@PathVariable String loanNumber, @RequestParam String updateDate,
										   @RequestParam String updateType) throws LoanProcessingException {
		return BANK_SERVICE.updateLoanDetails(loanNumber, updateDate, updateType);
	}

	@PatchMapping("loan/close/{loanNumber}")
	public final boolean closeLoan(@PathVariable String loanNumber, @RequestParam double payAmount) throws LoanProcessingException {
		return BANK_SERVICE.closeLoan(loanNumber, payAmount);
	}

	@PatchMapping("loan/repay/{loanNumber}")
	public final boolean repayLoanAmount(@PathVariable String loanNumber, @RequestParam double repaymentAmount) throws LoanProcessingException {
		return BANK_SERVICE.repayLoanAmount(loanNumber, repaymentAmount);
	}
}