package com.kbeauty.gbt.entity.enums;

public enum BstiType implements CodeVal{
	
	DSPW("0010", "DSPW"),
	DSPT("0020", "DSPT"),
	DSNW("0030", "DSNW"),
	DSNT("0040", "DSNT"),
	DRPW("0050", "DRPW"),
	DRPT("0060", "DRPT"),
	DRNW("0070", "DRNW"),
	DRNT("0080", "DRNT"),
	OSPW("0090", "OSPW"),
	OSPT("0100", "OSPT"),
	OSNW("0110", "OSNW"),
	OSNT("0120", "OSNT"),
	ORPW("0130", "ORPW"),
	ORPT("0140", "ORPT"),
	ORNW("0150", "ORNW"),
	ORNT("0160", "ORNT"),
	
	
	;
		
	private String code;
	private String val;
	
	BstiType(String code, String val){
		this.code = code;
		this.val = val;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
}
