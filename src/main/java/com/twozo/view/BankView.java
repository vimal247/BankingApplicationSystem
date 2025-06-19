package com.twozo.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.twozo.common.UserInputHandler;
import com.twozo.controller.BankController;
import com.twozo.controller.impl.TenkasiBankController;
import com.twozo.controller.impl.TirunelveliBankController;
import com.twozo.enums.AccountType;
import com.twozo.enums.CourseDuration;
import com.twozo.enums.EducationLevel;
import com.twozo.enums.EmploymentType;
import com.twozo.enums.Gender;
import com.twozo.enums.LoanAmount;
import com.twozo.enums.LoanTenure;
import com.twozo.enums.LoanType;
import com.twozo.enums.PropertyType;
import com.twozo.exception.account.AccountProcessingException;
import com.twozo.exception.account.InvalidBranchException;
import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.customer.InvalidCustomerAadharException;
import com.twozo.exception.customer.InvalidCustomerAddressException;
import com.twozo.exception.customer.InvalidCustomerDobException;
import com.twozo.exception.customer.InvalidCustomerNameException;
import com.twozo.exception.customer.InvalidMobileNoException;
import com.twozo.exception.customer.InvalidPanCardNumberException;
import com.twozo.exception.customer.InvalidPhotoUrlException;
import com.twozo.exception.database.DatabaseException;
import com.twozo.exception.inquiry.InquiryProcessingException;
import com.twozo.exception.loan.LoanProcessingException;
import com.twozo.exception.transaction.TransactionProcessingException;
import com.twozo.model.Account;
import com.twozo.model.Customer;
import com.twozo.model.EducationLoan;
import com.twozo.model.HomeLoan;
import com.twozo.model.Loan;
import com.twozo.model.PersonalLoan;
import com.twozo.model.Transaction;
import com.twozo.validation.AccountValidator;
import com.twozo.validation.CustomerValidator;

public class BankView {
	
	private BankController bankController = null;
	
	private static final UserInputHandler USER_INPUT_HANDLER = UserInputHandler.getInstance();

	public final void startApplication() {
		try {
			bankController = getBranch();
		} catch (InvalidBranchException e) {
			System.out.println("Validation Error : " + e.getMessage());
		}
		if (bankController != null) {
			boolean inMenu = true;

			while (inMenu) {
				displayMenu();
				final int choice = USER_INPUT_HANDLER.getInt();

				switch (choice) {
				case 0: {
					inMenu = false;
					bankController = null;
					System.out.println("Exited...!");
				}
					break;
				case 1: {
					handleAccountCreation();
				}
					break;
				case 2: {
					retrieveAccountDetails();
				}
					break;
				case 3: {
					handleAccountDetailsUpdate();
				}
					break;
				case 4: {
					handleAccountDeactivation();
				}
					break;
				case 5: {
					handleCustomerDetailsUpdate();
				}
					break;
				case 6: {
					retrieveCustomerDetails();
				}
					break;
				case 7: {
					handleBalanceEnquiry();
				}
					break;
				case 8: {
					handleDepositAmount();
				}
					break;
				case 9: {
					handleWithdrawAmount();
				}
					break;
				case 10: {
					handleFundTransfer();
				}
					break;
				case 11: {
					retrieveTransactionRecords();
				}
					break;
				case 12: {
					handleApplyLoan();
				}
					break;
				case 13: {
					handleUpdateLoan();
				}
					break;
				case 14: {
					retrieveLoanDetails();
				}
					break;
				case 15: {
					handleCloseLoan();
				}
					break;
				case 16: {
					handleLoanRepament();
				}
					break;
				default:
					System.out.println(" INVALID KEY!");
				}
			}
		} else {
			System.out.println("Null...");
		}
	}

	private BankController getBranch() throws InvalidBranchException {
		System.out.println(
				"Which Branch You Want To Visit In This...! \n1 - Tenkasi. \n2 - Tirunelveli. \nEnter Your Choice!");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return TenkasiBankController.getInstance();
		}
		case 2: {
			return TirunelveliBankController.getInstance();
		}
		default:
			throw new InvalidBranchException("Invalid Branch.");
		}
	}

	private static void displayMenu() {
		System.out.println("- - - - - - - - - - - - - - - - -|" + " \n       WELCOME TO IOB BANK       |"
				+ " \n- - - - - - - - - - - - - - - - -|" + " \n0 - Exit.                        |"
				+ " \n1 - Create Account.              |" + " \n2 - Get Account Details.         |"
				+ " \n3 - Update Account Details.      |" + " \n4 - Deactivate Account.          |"
				+ " \n5 - Update Customer Details.     |" + " \n6 - Get Customer Details.        |"
				+ " \n7 - Enquire Balance.             |" + " \n8 - Deposit Amount.              |"
				+ " \n9 - Withdraw Amount.		 |" + " \n10 - Transfer Amount.     	 |"
				+ " \n11 - Get Transaction History     |" + " \n12 - Apply Loan.                 |"
				+ " \n13 - Update Loan Details.        |" + " \n14 - Get Loan Details.           |"
				+ " \n15 - Close Loan.                 |" + "\n16 - Loan Amount Re-Payment.     |"
				+ " \n- - - - - - - - - - - - - - - - -| " + " \nWhat do You Want in this..?"
				+ " \nEnter Your Choice : ");
	}

	private static String getAccountNumber() {
		System.out.println("Enter Your Account Number : ");

        return USER_INPUT_HANDLER.getString();
	}

	private void handleAccountCreation() {
		try {
			final Account account = getCustomerDetails();

			if (CustomerValidator.validateDetail(account.getCustomer())) {
				if (AccountValidator.validationAccount(account)) {
					final boolean accountCreated = bankController.createAccount(account);

					if (accountCreated) {
						displayAccountDetails(account);
					} else {
						System.out.println("Account Can't Created.");
					}
				}
			}
		} catch (AccountProcessingException | CustomerProcessingException | InvalidCustomerDobException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void retrieveAccountDetails() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountAvailableInBranch(accountNumber)) {
				final Account account = bankController.getAccountDetails(accountNumber);

				if (account != null) {
					displayAccountDetails(account);	
				}
			}
		} catch (AccountProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleAccountDetailsUpdate() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountAvailableInBranch(accountNumber)) {
				final AccountType accountType = getAccountType();

				if (bankController.updateAccountType(accountNumber, accountType)) {
					System.out.println("Updated Successfully.");
				}
			}
		} catch (AccountProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleAccountDeactivation() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountAvailableInBranch(accountNumber)) {
				System.out.println("Do You Want Deactivate The Account (yes/no) : ");
				String confirmation = USER_INPUT_HANDLER.getString();

				if (confirmation.equalsIgnoreCase("yes")) {
					if (bankController.deactivateAccount(accountNumber)) {
						System.out.println("Successfully Deactivated.");
					}
				} else {
					System.out.println("Deactivate Process Canceled...");
				}
			}
		} catch (AccountProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleCustomerDetailsUpdate() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountAvailableInBranch(accountNumber)) {
				if (updateCustomerDetail(accountNumber)) {
					System.out.println("Successfully Updated.");
				} else {
					System.out.println("Updation Failed.");
				}
			}
		} catch (CustomerProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void retrieveCustomerDetails() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountAvailableInBranch(accountNumber)) {
				final Customer customer = bankController.getCustomerDetails(accountNumber);

				if (customer != null) {
					displayCustomerDetails(customer);
				}
			}
		} catch (CustomerProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleBalanceEnquiry() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountPresent(accountNumber)) {
				final double accountBalance = bankController.enquireBalance(accountNumber);
				System.out.println("Account Number : " + accountNumber + "\nYour Account Balance : " + accountBalance);
			}
		} catch (InquiryProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleDepositAmount() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountPresent(accountNumber)) {
				System.out.println("Enter Your Deposit Amount");
				final double amount = USER_INPUT_HANDLER.getDouble();
				final boolean deposit = bankController.depositAmount(accountNumber, amount);

				if (deposit) {
					System.out.println("Deposited Amount : " + amount);
				}
			}
		} catch (TransactionProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleWithdrawAmount() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountPresent(accountNumber)) {
				System.out.println("Enter Your Withdraw Amount : ");
				final double amount = USER_INPUT_HANDLER.getDouble();

				if (bankController.withdrawAmount(accountNumber, amount)) {
					System.out.println("Withdraw Successfully " + amount);
				}
			}
		} catch (TransactionProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleFundTransfer() {
		final String senderAccountNumber = getAccountNumber();

		try {
			if (bankController.isAccountPresent(senderAccountNumber)) {
				System.out.println("Enter Your Credit Amount : ");
				final double amount = USER_INPUT_HANDLER.getDouble();

				System.out.println("Enter Your Transfer Account Number");
				String receiverAccountNumber = USER_INPUT_HANDLER.getString();

				if (bankController.isAccountPresent(receiverAccountNumber)) {
					if (bankController.transferFunds(senderAccountNumber, amount, receiverAccountNumber)) {
						System.out.println("RS." + amount + " Debited " + senderAccountNumber + " AccBal : "
								+ bankController.enquireBalance(senderAccountNumber));
						System.out.println("RS." + amount + " Credited " + receiverAccountNumber + " AccBal : "
								+ bankController.enquireBalance(receiverAccountNumber));
					} else {
						System.out.println("Transaction Failed.");
					}
				}
			}
		} catch (TransactionProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void retrieveTransactionRecords() {
		final String accountNumber = getAccountNumber();

		try {
			if (bankController.isAccountPresent(accountNumber)) {
				final List<Transaction> retrievedData = bankController.getTransactionHistory(accountNumber);

				if (!(retrievedData.isEmpty())) {
					for (Transaction transactions : retrievedData) {
						System.out.println(transactions);	
					}
				} else {
					System.out.println("No Transaction Data Present.");
				}
			}
		} catch (TransactionProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleApplyLoan() {
		try {
			final String accountNumber = getAccountNumber();
			if (bankController.isAccountPresent(accountNumber)) {

				System.out.println("Choose Your Loan Type : \n1 - Home Loan \n2 - Personal Loan \n3 - Education Loan");
				final int choice = USER_INPUT_HANDLER.getInt();

				switch (choice) {
				case 1: {
					final HomeLoan homeLoan = getHomeLoanInfo(accountNumber);

					if (homeLoan != null) {
						if (bankController.applyloan(homeLoan)) {
							System.out.println("Successfully Applied Home Loan");
							System.out.println(homeLoan);
						}
					}
				}
					break;
				case 2: {
					final PersonalLoan personalLoan = getPersonalLoanInfo(accountNumber);

					if (personalLoan != null) {
						if (bankController.applyloan(personalLoan)) {
							System.out.println("Successfully Applied Personal Loan");
							System.out.println(personalLoan);
						}
					}
				}
					break;
				case 3: {
					final EducationLoan educationLoan = getEducationLoanInfo(accountNumber);

					if (educationLoan != null) {
						if (bankController.applyloan(educationLoan)) {
							System.out.println("Successfully Applied Education Loan");
							System.out.println(educationLoan);
						}
					}
				}
					break;
				default:
					System.out.println("Enter a Correct Key..!");
				}
			}
		} catch (LoanProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}

	}

	private void handleUpdateLoan() {
		System.out.println("Enter Your Loan ID : ");
		final String loanId = USER_INPUT_HANDLER.getString();

		try {
			if (bankController.isLoanNumberPresent(loanId)) {
				if (updateLoanDetails(loanId)) {
					System.out.println("Successfully Updated.");
				} else {
					System.out.println("Updation Failed.");
				}
			} else {
				System.out.println("Enter Your Correct Loan Number.");
			}
		} catch (LoanProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void retrieveLoanDetails() {
		System.out.println("Enter Your Loan Number : ");
		final String loanNumber = USER_INPUT_HANDLER.getString();

		try {
			if (bankController.isLoanNumberPresent(loanNumber)) {
				final Loan loan = bankController.getLoanDetails(loanNumber);
			
				if (loan != null) {
					System.out.println(loan);
				}
			} else {
				System.out.println("Enter Your Correct Loan Number.");
			}
		} catch (LoanProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		}
	}

	private boolean updateLoanDetails(final String loanNumber) throws LoanProcessingException {
		boolean console = true;

		while (console) {
			System.out.println("Which Details You Want To Update ? \n1 - Update Name. \n2 - Update Mobile Number. "
					+ "\n3 - Update DOB. \n4 - Update Address. \n5 - Update Photo URL.");

			final int choice = USER_INPUT_HANDLER.getInt();

			switch (choice) {
			case 0: {
				console = false;
				System.out.println("Exited...!");
			}
				break;
			case 1: {
				System.out.println("Enter Your New Name : ");
				final String name = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerName(name)) {
						final String updateType = "name";
						
						return bankController.updateLoanDetails(loanNumber, name, updateType);
					}
				} catch (InvalidCustomerNameException | LoanProcessingException e) {
					throw new LoanProcessingException("Loan error : ", e);
				}
				return false;
			}
			case 2: {
				System.out.println("Enter Your New Mobile Number : ");
				final String mobileNumber = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerMobileNo(mobileNumber)) {
						final String updateType = "mobile_Number";
						
						return bankController.updateLoanDetails(loanNumber, mobileNumber, updateType);
					}
				} catch (InvalidMobileNoException | LoanProcessingException e) {
					throw new LoanProcessingException("Loan error : ", e);
				}
				return false;
			}
			case 3: {
				System.out.println("Enter Your New DOB : ");
				final String newDob = USER_INPUT_HANDLER.getString();
				final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				final LocalDate dobDate = LocalDate.parse(newDob, formatter);

				try {
					if (CustomerValidator.validateCustomerDob(dobDate)) {
						final String updateType = "dob";
						
						return bankController.updateLoanDetails(loanNumber, newDob, updateType);
					}
				} catch (InvalidCustomerDobException | LoanProcessingException e) {
					throw new LoanProcessingException("Loan error : ", e);
				}
				return false;
			}
			case 4: {
				System.out.println("Enter Your New Address : ");
				final String address = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerAddress(address)) {
						final String updateType = "address";
						
						return bankController.updateLoanDetails(loanNumber, address, updateType);
					}
				} catch (InvalidCustomerAddressException | LoanProcessingException e) {
					throw new LoanProcessingException("Loan error : ", e);
				}
				return false;
			}
			case 5: {
				System.out.println("Enter Your New Photo URL : ");
				final String photoUrl = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerPhoto(photoUrl)) {
						final String updateType = "photoUrl";
						
						return bankController.updateLoanDetails(loanNumber, photoUrl, updateType);
					}
				} catch (InvalidPhotoUrlException | LoanProcessingException e) {
					throw new LoanProcessingException("Loan error : ", e);
				}
				return false;
			}

			default: {
				System.out.println("Enter a Valid Number");
			}
			}
		}
		return false;
	}

	private void handleCloseLoan() {
		System.out.println("Enter Your Loan ID : ");
		final String loanNumber = USER_INPUT_HANDLER.getString();

		try {
			if (bankController.isLoanNumberPresent(loanNumber)) {
				final Loan loan = bankController.getLoanDetails(loanNumber);
				System.out.println("Your Outstanding Loan Balance : " + loan.getBalanceAmount()
						+ "\n Have you paid the full amount? (Yes/No): ");
				final String getconfirmation = USER_INPUT_HANDLER.getString();

				if (getconfirmation.equalsIgnoreCase("yes")) {
					System.out.println("Enter the amount you have paid: ");
					final double payAmount = USER_INPUT_HANDLER.getDouble();

					if (bankController.closeLoan(loanNumber, payAmount)) {
						System.out.println("Loan Closed Successfully. Thank you!");
					} else {
						System.out.println("Loan closure failed. Please check the amount or try again.");
					}
				} else {
					System.out.println("Loan process canceled..!");
				}
			} else {
				System.out.println("Enter Your Correct Loan Number.");
			}
		} catch (LoanProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private void handleLoanRepament() {	
		System.out.println("Please enter your Loan ID: ");
		final String loanNumber = USER_INPUT_HANDLER.getString();

		try {
			if (bankController.isLoanNumberPresent(loanNumber)) { 
				final Loan loan = bankController.getLoanDetails(loanNumber);
				System.out.println("Your Emi Amount : " + loan.getEmiAmount()
						+ " \nDo you want to proceed with the payment? (yes/no)");
				final String getconfirmation = USER_INPUT_HANDLER.getString();

				if (getconfirmation.equalsIgnoreCase("yes")) {
					System.out.println("Enter the amount you are paying: ");
					final double amount = USER_INPUT_HANDLER.getDouble();
					
					if (bankController.repayLoanAmount(loanNumber, amount)) {
						System.out.println("EMI payment successful. Thank you!");
					} else {
						System.out.println("Payment failed. Please check the amount and try again.");
					}
				} else {
					System.out.println("Loan repayment process has been canceled..");
				}
			} else {
				System.out.println("Enter Your Correct Loan Number.");
			}
		} catch (LoanProcessingException e) {
			System.out.println("Validation Error : " + e.getMessage());
		} catch (Exception e) {
			System.out.println("Unexpected Error : " + e.getMessage());
		}
	}

	private Customer getCustomerInfo() throws InvalidCustomerDobException {
		System.out.println("Enter Your Name :");
		final String name = USER_INPUT_HANDLER.getString();

		System.out.println("Select Your Gender : \n1 - Male. \n2 - Female.");
		final Gender gender = getCustomerGender();

		System.out.println("Enter Your 10 DIGIT MobileNo : ");
		final String mobileNumber = USER_INPUT_HANDLER.getString();

		System.out.println("Enter Your 12 DIGIT AadharNo : ");
		final String aadharNumber = USER_INPUT_HANDLER.getString();

		System.out.println("5 letters + 4 digits + 1 letter (e.g., ABCDE1234F)" + " \nEnter Your PAN CARD Number : ");
		final String panCardNumber = USER_INPUT_HANDLER.getString();

		System.out.println("Enter Your Customer DOB Like \"yyyy-MM-dd\" : ");
		final LocalDate dob = USER_INPUT_HANDLER.getDate();

		System.out.println("Enter Your Address : ");
		final String address = USER_INPUT_HANDLER.getString();

		System.out.println("Enter Your Photo URL : \n(e.g., image.jpeg|image.jpg|image.png) ");
		final String photoUrl = USER_INPUT_HANDLER.getString();

		return new Customer(name, gender, mobileNumber, aadharNumber, panCardNumber, dob, address, photoUrl);
	}

	private HomeLoan getHomeLoanInfo(final String accountNumber) throws InvalidCustomerNameException,
			InvalidCustomerAadharException, InvalidCustomerAddressException, InvalidCustomerDobException,
			InvalidMobileNoException, InvalidPanCardNumberException, InvalidPhotoUrlException, DatabaseException {
		final Loan loan = getLoanInfo(accountNumber);

		final EmploymentType employementType = getEmpolymentType();

		System.out.println("Enter Your Monthly Income : ");
		final double income = USER_INPUT_HANDLER.getDouble();

		System.out.println("Enter Your Bank Statement URL : ");
		final String bankStatementUrl = USER_INPUT_HANDLER.getString();

		System.out.println("Enter Your Property Address : ");
		final String propertyAddress = USER_INPUT_HANDLER.getString();

		final PropertyType propertyType = getPropertyType();

		final HomeLoan homeLoan = new HomeLoan(null, accountNumber, loan.getLoanAmount(), loan.getLoanAmount(),
				LoanType.HOME_LOAN, loan.getLoanTenure(), null, null, income, employementType, 0,
				bankStatementUrl, propertyAddress, propertyType);

		return homeLoan;
	}

	private PropertyType getPropertyType() {
		System.out.println("Enter Your Property Type : \n1 - VILLA \n2 - APARTMENT \n3 - LAND "
				+ "\n4 - INDEPENDENT HOUSE \n5 - FARM HOUSE \n6 - COMMERCIAL PROPERTY");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return PropertyType.VILLA;
		}
		case 2: {
			return PropertyType.APARTMENT;	
		}
		case 3: {
			return PropertyType.LAND;	
		}
		case 4: {
			return PropertyType.INDEPENDENT_HOUSE;	
		}
		case 5: {
			return PropertyType.FARM_HOUSE;	
		}
		case 6: {
			return PropertyType.COMMERCIAL_PROPERTY;	
		}
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return getPropertyType();
	}

	private PersonalLoan getPersonalLoanInfo(final String accountNumber) {
		final Loan loan = getLoanInfo(accountNumber);
		final EmploymentType employementType = getEmpolymentType();

		System.out.println("Enter Your Monthly Income : ");
		final double income = USER_INPUT_HANDLER.getDouble();

		final PersonalLoan personalLoan = new PersonalLoan(null, loan.getAccountNumber(), loan.getLoanAmount(),
				loan.getLoanAmount(), LoanType.PERSONAL_LOAN, loan.getLoanTenure(), null, income,
				employementType, 0);

		return personalLoan;
	}

	private EducationLoan getEducationLoanInfo(final String accountNumber) {
		final Loan loan = getLoanInfo(accountNumber);

		System.out.println("Enter Your Bank Statement URL : ");
		final String bankStatementUrl = USER_INPUT_HANDLER.getString();

		final EducationLevel educationLevel = getEducationLevel();

		System.out.println("Enter Your Institution Name : ");
		final String institutionName = USER_INPUT_HANDLER.getString();

		System.out.println("Enter Your Course Name : ");
		final String courseName = USER_INPUT_HANDLER.getString();

		final CourseDuration courseDuration = getCourseDuration();

		final EducationLoan educationLoan = new EducationLoan(null, loan.getAccountNumber(), loan.getLoanAmount(),
				loan.getLoanAmount(), LoanType.EDUCATION_LOAN, loan.getLoanTenure(), 0, null,
				bankStatementUrl, educationLevel, institutionName, courseName, courseDuration);

		return educationLoan;
	}
	
	private EducationLevel getEducationLevel() {
		System.out.println("Select Education Level : \n1 -  UNDERGRADUATE \n2 - POSTGRADUATE "
				+ "\n3 - DOCTORAL \n4 - DIPLOMA \n5 - OVERSEAS");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return EducationLevel.UNDERGRADUATE;
		}
		case 2: {
			return EducationLevel.POSTGRADUATE;
		}
		case 3: {
			return EducationLevel.DOCTORAL;
		}
		case 4: {
			return EducationLevel.DIPLOMA;
		}
		case 5: {
			return EducationLevel.OVERSEAS;
		}
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return getEducationLevel();
	}
	
	private CourseDuration getCourseDuration() {
		System.out.println("Enter Your Course Duration : \n1 - ONE_YEAR \n2 - TWO_YEARS "
				+ "\n3 - THREE_YEARS \n4 - FOUR_YEARS \n5 - FIVE_YEARS");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return CourseDuration.ONE_YEAR;
		}
		case 2: {
			return CourseDuration.TWO_YEARS;
		}
		case 3: {
			return CourseDuration.THREE_YEARS;
		}
		case 4: {
			return CourseDuration.FOUR_YEARS;
		}
		case 5: {
			return CourseDuration.FIVE_YEARS;
		}
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return getCourseDuration();
	}

	private Gender getCustomerGender() {
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return Gender.MALE;
		}
		case 2: {
			return Gender.FEMALE;
		}
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return getCustomerGender();
	}

	private double getLoanAmount() {
		System.out.println(
				"Choose Your Loan Amount : \n1 - Fifty Thousand \n2 - One Lakh \n3 - Five Lakh " + "\n4 - Ten Lakh");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return LoanAmount.FIFTY_THOUSAND.getAmount();
		}
		case 2: {
			return LoanAmount.ONE_LAKH.getAmount();
		}
		case 3: {
			return LoanAmount.FIVE_LAKH.getAmount();
		}
		case 4: {
			return LoanAmount.TEN_LAKH.getAmount();
		}
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return getLoanAmount();
	}

	private Loan getLoanInfo(final String accountNumber) {
		final Double loanAmount = getLoanAmount();
		final LoanTenure loanTenure = selectLoanTenure();

		return new Loan(accountNumber, loanAmount, loanAmount, null, loanTenure, 0, null);
	}

	private EmploymentType getEmpolymentType() {
		System.out.println("Enter Your Empolyement ? \n1 - Self-Empolyee \n2 - Salaried");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return EmploymentType.SELF_EMPLOYED;
		}
		case 2: {
			return EmploymentType.SALARIED;
		}
		case 3: {
			return EmploymentType.BUSINESS_OWNER;
		}
		case 4: {
			return EmploymentType.RETIRED;
		}
		case 5: {
			return EmploymentType.UNEMPLOYED;
		}
		case 6: {
			return EmploymentType.FREELANCER;
		}
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return getEmpolymentType();
	}

	private LoanTenure selectLoanTenure() {
		System.out.println("Enter Your Loan Tenure : \n1 - One Year. \n2 - Three Year.");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return LoanTenure.ONE_YEAR;
		}
		case 2: {
			return LoanTenure.TWO_YEARS;	
		}
		case 3: {
			return LoanTenure.THREE_YEARS;	
		}
		case 4: {
			return LoanTenure.FOUR_YEARS;
		} 
		case 5: {
			return LoanTenure.FIVE_YEARS;
		} 
		default:
			System.out.println("Enter a Correct Key..!");
		}
		return selectLoanTenure();
	}

	private Account getCustomerDetails() throws InvalidCustomerDobException {
		final Customer customer = getCustomerInfo();

		System.out.println("Select Your Account Type : ");
		final AccountType accountType = getAccountType();

		System.out.println("Enter Your Amount: ");
		final Double balance = USER_INPUT_HANDLER.getDouble();

		return new Account(accountType, balance, customer);
	}

	private AccountType getAccountType() {
		System.out
				.println("1 - Savings Account \n2 - Current Account \n3 - Fixed Deposit " + " \n4 - Recurring Deposit");
		final int choice = USER_INPUT_HANDLER.getInt();

		switch (choice) {
		case 1: {
			return AccountType.SAVINGS;
		}
		case 2: {
			return AccountType.CURRENT;
		}
		case 3: {
			return AccountType.FIXED_DEPOSIT;
		}
		case 4: {
			return AccountType.RECURRING_DEPOSIT;
		}
		default:
			System.out.println("Invalid Number,Try Again.");
		}
		return getAccountType();
	}

	private void displayAccountDetails(final Account account) {
		System.out.println("- - - - - - - - - - - - - - - - - - - - - - - -|"
				+ " \n              ACCOUNT DETAILS                  |"
				+ " \n- - - - - - - - - - - - - - - - - - - - - - - -|" + " \nBank Name : " + Account.getBankName()
				+ "                                |" + " \nAccount Number : " + account.getAccountNumber()
				+ "            |" + " \nAccount IFSC Code : " + account.getIfscCode() + "	       |"
				+ " \nBranch Name : " + account.getBranchName().name() + "                          |"
				+ " \nAccount Type : " + account.getAccountType().name() + "                 |"
				+ " \nAccount Balance : Rs." + account.getBalance() + "                     |"
				+ " \n- - - - - - - - - - - - - - - - - - - - - - - -|"
				+ " \n  *** IOB Bank Glad To Associate With You *** \n");
	}

	private void displayCustomerDetails(final Customer customer) {
		System.out.println(
				"- - - - - - - - - - - - - - -| \n       CUSTOMER DETAILS      | \n- - - - - - - - - - - - - - -|"
						+ " \nName : " + customer.getName() + "                 |" + " \nGender : "
						+ customer.getGender() + "                |" + " \nMobile No : " + customer.getMobileNumber()
						+ "       |" + " \nAadhar No : " + customer.getAadharNumber() + "     |" + " \nDate Of Birth : "
						+ customer.getDob() + "   |" + " \nAddress : " + customer.getAddress() + "            |"
						+ " \n- - - - - - - - - - - - - - -|");
	}

	private boolean updateCustomerDetail(final String accountNumber) throws CustomerProcessingException {
		boolean consoleStart = true;

		while (consoleStart) {
			System.out.println("Which Detail To Update: \n0 - Exit \n1 - Update Name "
					+ " \n2 - Update MobileNo \n3 - Update Dob \n4 - Update Address.");

			final int key = USER_INPUT_HANDLER.getInt();

			switch (key) {
			case 0: {
				consoleStart = false;
				System.out.println("Exited");
			}
				break;
			case 1: {
				System.out.println("Enter Your Customer Name :");
				final String customerName = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerName(customerName)) {
						final String updateType = "name";
						return bankController.updateCustomerDetail(customerName, accountNumber, updateType);
					}
				} catch (InvalidCustomerNameException | CustomerProcessingException e) {
					throw new CustomerProcessingException("Customer error : ", e);
				}
				return false;
			}
			case 2: {
				System.out.println("Enter Your 10 DIGIT MobileNo : ");
				final String mobileNumber = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerMobileNo(mobileNumber)) {
						final String updateType = "mobile_number";
						return bankController.updateCustomerDetail(mobileNumber, accountNumber, updateType);
					}
				} catch (InvalidMobileNoException | CustomerProcessingException e) {
					throw new CustomerProcessingException("Customer error : ", e);
				}
				return false;
			}
			case 3: {
				System.out.println("Enter Your Customer DOB Like \"dd-MM-yyyy\" : ");
				final String customerDob = USER_INPUT_HANDLER.getString();
				final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				final LocalDate dobDate = LocalDate.parse(customerDob, formatter);

				try {
					if (CustomerValidator.validateCustomerDob(dobDate)) {
						String updateType = "dob";
						return bankController.updateCustomerDetail(customerDob, accountNumber, updateType);
					}
				} catch (InvalidCustomerDobException | CustomerProcessingException e) {
					throw new CustomerProcessingException("Customer error : ", e);
				}
				return false;
			}
			case 4: {
				System.out.println("Enter Your Address : ");
				final String customerAddress = USER_INPUT_HANDLER.getString();

				try {
					if (CustomerValidator.validateCustomerAddress(customerAddress)) {
						final String updateType = "address";
						return bankController.updateCustomerDetail(customerAddress, accountNumber, updateType);
					}
				} catch (InvalidCustomerAddressException | CustomerProcessingException e) {
					throw new CustomerProcessingException("Customer error : ", e);
				}
				return false;
			}
			}
		}
		return false;
	}
}