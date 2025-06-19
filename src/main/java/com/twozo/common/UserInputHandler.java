package com.twozo.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import com.twozo.exception.customer.InvalidCustomerDobException;

public final class UserInputHandler {

	private static UserInputHandler instance;
	private static final Scanner SCANNER_INPUT = new Scanner(System.in);

	public static final UserInputHandler getInstance() {
		if (instance == null) {
			instance = new UserInputHandler();
		}
		return instance;
	}

	// Private Constructor To Restrict Instantiation Outside The Class.
	private UserInputHandler() {

	}

	public final int getInt() {
		while (true) {
			if (SCANNER_INPUT.hasNextInt()) {
				return SCANNER_INPUT.nextInt();
			} else {
				System.out.println("Enter a valid number:");
				SCANNER_INPUT.next();
			}
		}
	}

	public final LocalDate getDate() throws InvalidCustomerDobException {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		 while (true) {
		        final String date = SCANNER_INPUT.next().trim();
		        try {
		            return LocalDate.parse(date, formatter);
		        } catch (DateTimeParseException e) {
		            throw new InvalidCustomerDobException("Invalid date format. Please enter the date in yyyy-MM-dd format.");
		        }
		    }
	}

	public final String getString() {
		while (true) {
			final String value = SCANNER_INPUT.next().trim();
			
			if (!value.isEmpty()) {
				return value;
			}
			System.out.println("Enter a valid string:");
		}
	}

	public final long getLong() {
		while (true) {
			if (SCANNER_INPUT.hasNextLong()) {
				return SCANNER_INPUT.nextLong();
			} else {
				System.out.println("Enter a valid long number:");
				SCANNER_INPUT.next();
			}
		}
	}

	public final double getDouble() {
		while (true) {
			if (SCANNER_INPUT.hasNextDouble()) {
				return SCANNER_INPUT.nextDouble();
			} else {
				System.out.println("Enter a valid double number:");
				SCANNER_INPUT.next();
			}
		}
	}

}
