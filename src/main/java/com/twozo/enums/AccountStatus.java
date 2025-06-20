package com.twozo.enums;

public enum AccountStatus {
	ACTIVE("active"),
    INACTIVE("inactive"),
    CLOSED("closed"),
    SUSPENDED("suspended"),
    PENDING_ACTIVATION("pending activation"), 
    DORMANT("dormant"),
    FROZEN("frozen"),
    RESTRICTED("restricted"),
    BLOCKED("blocked"),
    UNDER_REVIEW("under review"),
    CLOSED_PENDING_SETTLEMENT("closed pending settlement"),
    ARCHIVED("archived"),
    CHARGE_OFF("charge off"),
    REACTIVATED("reactivated");
	
	private final String description;

	AccountStatus(final String description) {
		this.description = description;
	}
 
	public final String getDescription() {
		return description;
	}
}
