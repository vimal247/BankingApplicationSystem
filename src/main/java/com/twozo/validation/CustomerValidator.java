package com.twozo.validation;

import java.time.LocalDate;
import java.time.Period;

import com.twozo.exception.customer.CustomerProcessingException;
import com.twozo.exception.customer.InvalidCustomerAadharException;
import com.twozo.exception.customer.InvalidCustomerAddressException;
import com.twozo.exception.customer.InvalidCustomerDobException;
import com.twozo.exception.customer.InvalidCustomerNameException;
import com.twozo.exception.customer.InvalidMobileNoException;
import com.twozo.exception.customer.InvalidPanCardNumberException;
import com.twozo.exception.customer.InvalidPhotoUrlException;
import com.twozo.model.Customer;

public final class CustomerValidator {

	private static final int MIN_NAME_SIZE = 5;
	private static final int MAX_NAME_SIZE = 15;
	private static final int MAX_MBL_NUM_LENGHT = 10;
	private static final int MAX_AADHAR_NUM_LENGHT = 12;

	private static CustomerValidator instance;

	// Singleton method for CustomerValidator, ensuring a single instance of the
	// class.
	public final static CustomerValidator getInstance() {
		if (instance == null) {
			instance = new CustomerValidator();
		}
		return instance;
	}

	// Private constructor to restrict instantiation outside the class, enforcing
	// Singleton pattern.
	private CustomerValidator() {

	}

	// Validates the customer details.
	public static boolean validateDetail(final Customer customer) throws CustomerProcessingException {
		final String customerName = customer.getName();
		final String mobileNo = customer.getMobileNumber();
		final String aadharNo = customer.getAadharNumber();
		final LocalDate customerDob = customer.getDob();
		final String address = customer.getAddress();
		final String PanCardNumber = customer.getPanCardNumber();
		final String customerPhotoUrl = customer.getPhotoUrl();

		try {
			return validateCustomerName(customerName) && validateCustomerMobileNo(mobileNo)
					&& validateCustomerAadharNo(aadharNo) && validateCustomerDob(customerDob)
					&& validateCustomerAddress(address) && validateCustomerPanNumber(PanCardNumber)
					&& validateCustomerPhoto(customerPhotoUrl);
		} catch (InvalidCustomerNameException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		} catch (InvalidMobileNoException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		} catch (InvalidCustomerAadharException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		} catch (InvalidCustomerDobException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		} catch (InvalidCustomerAddressException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		} catch (InvalidPanCardNumberException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		} catch (InvalidPhotoUrlException e) {
			throw new CustomerProcessingException("Customer error : " + e.getMessage(), e);
		}
	}

	// Validates the customer name.
	public static final boolean validateCustomerName(final String customerName) throws InvalidCustomerNameException {
		if (customerName == null || customerName.isEmpty()) {
			throw new InvalidCustomerNameException("Customer Name is Empty");
		}
		if (customerName.matches(".*\\d.*")) {
			throw new InvalidCustomerNameException("Customer Name Should Not Contain Number");
		}
		if (customerName.startsWith(" ") || customerName.endsWith(" ")) {
			throw new InvalidCustomerNameException(customerName + " Customer Name Can't Start & End with the Spaces");
		}
		if (customerName.contains("  ")) {
			throw new InvalidCustomerNameException("Customer Name Can't Contain Multiple Spaces");
		}
		if (customerName.length() < MIN_NAME_SIZE || customerName.length() > MAX_NAME_SIZE) {
			throw new InvalidCustomerNameException("Customer Name Must Above " + MIN_NAME_SIZE
					+ " Character and Maximum " + MAX_NAME_SIZE + " characters");
		}
		return true;
	}

	// Validates the customer mobile number.
	public static final boolean validateCustomerMobileNo(final String mobileNo) throws InvalidMobileNoException {
		if (mobileNo.matches(".*\\D+.*")) {
			throw new InvalidMobileNoException("Mobile Number Contain Only Number");
		}
		if (mobileNo.length() != 10) {
			throw new InvalidMobileNoException(
					"You Entered " + mobileNo.length() + " Degits, Mobile Number Contains 10 Degits Only.");
		}
		char firstDigit = mobileNo.charAt(0);

		if (firstDigit < '6' || firstDigit > '9') {
			throw new InvalidMobileNoException("Invalid Indian Mobile Number.It Must Start With 9, 8, 7, 6.");
		}
		if (mobileNo.contains(" ")) {
			throw new InvalidMobileNoException("Mobile Number Doesn't Contain Space.");
		}
		if (mobileNo.length() != MAX_MBL_NUM_LENGHT) {
			throw new InvalidMobileNoException(
					"Actual Mobile Number Size Is 10 Digit," + "But You Entered Digits = " + mobileNo.length());
		}
		return true;
	}

	// Validates the customer Aadhar number.
	public static final boolean validateCustomerAadharNo(final String aadharNo) throws InvalidCustomerAadharException {
		if (aadharNo.matches(".*\\D+.*")) {
			throw new InvalidCustomerAadharException("Aadhar Number Contain Only Numbers.");
		}
		if (aadharNo.contains(" ")) {
			throw new InvalidCustomerAadharException("Aadhar Number Can't Contain Space.");
		}
		if (aadharNo.length() != MAX_AADHAR_NUM_LENGHT) {
			throw new InvalidCustomerAadharException(
					"Actual Aadhar Number Size Is 12 Digit, " + "But Your Entered Digits = " + aadharNo.length());
		}
		return true;
	}

	// Validates the customer date of birth (DOB).
	public static final boolean validateCustomerDob(final LocalDate customerDob) throws InvalidCustomerDobException {
		if (customerDob == null) {
			throw new InvalidCustomerDobException("DOB cannot be null");
		}
		LocalDate today = LocalDate.now();

		if (customerDob.isAfter(today)) {
			throw new InvalidCustomerDobException("DOB cannot be in the future");
		}
		// Check if customer is at least 18 years old
		int age = Period.between(customerDob, today).getYears();

		if (age < 18) {
			throw new InvalidCustomerDobException("Customer must be at least 18 years old");
		}
		return true;
	}

	// Validation the customer address.
	public static final boolean validateCustomerAddress(final String address) throws InvalidCustomerAddressException {
		if (address.startsWith(" ") || address.endsWith(" ")) {
			throw new InvalidCustomerAddressException("Address Can't Start & End With Space.");
		}
		if (address.contains("  ")) {
			throw new InvalidCustomerAddressException("Remove Unwanted Space Between The Address.");
		}
		return true;
	}

	public static final boolean validateCustomerPanNumber(final String customerPanNumber)
			throws InvalidPanCardNumberException {
		String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
		if ((!customerPanNumber.isEmpty()) && customerPanNumber.matches(panPattern)) {
			return true;
		}
		throw new InvalidPanCardNumberException("Invalid Pan Card Number.");
	}

	public static final boolean validateCustomerPhoto(final String customerPhotoUrl) throws InvalidPhotoUrlException {
		String photoPattern = ".*\\.(jpeg|jpg|png)$";
		if ((!customerPhotoUrl.isEmpty()) && customerPhotoUrl.toLowerCase().matches(photoPattern)) {
			return true;
		}
		throw new InvalidPhotoUrlException("Invalid Photo Url.");
	}
}
