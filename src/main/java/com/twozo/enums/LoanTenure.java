package com.twozo.enums;

public enum LoanTenure {

	ONE_YEAR(1), TWO_YEARS(2), THREE_YEARS(3), FOUR_YEARS(4), FIVE_YEARS(5);

	private final int years;

	LoanTenure(int years) {
		this.years = years;
	}

	public int getYears() {
		return years;
	}
}
