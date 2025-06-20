package com.twozo.enums;

public enum CourseDuration {

	ONE_YEAR(1), TWO_YEARS(2), THREE_YEARS(3), FOUR_YEARS(4), FIVE_YEARS(5);

	private final int years;

	CourseDuration(int years) {
		this.years = years;
	}

	public int getYears() {
		return years;
	}
}
