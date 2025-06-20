package com.twozo.enums;

public enum BranchCode {

	TIRUNELVELI(00110), 
	TENKASI(11001);
	
	private final int code;

    BranchCode(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    
    public static BranchCode fromCode(final int code) {
        for (BranchCode bc : BranchCode.values()) {
            if (bc.getCode() == code) {
                return bc;
            }
        }
        throw new IllegalArgumentException("Invalid BranchCode: " + code);
    }
}
