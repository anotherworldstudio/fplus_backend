package com.kbeauty.gbt.entity.enums;

public enum SkinAreaTreat {
	WRINKLE("10300", "주름"),
	PORE("10310", "모공"),
	TROUBLE("10320","Trouble"),
	PIGMENT("10330","Pigment"),
	REDNESS("10340","Redness")
	;
		
	private String code;	
	private String val;
	
	SkinAreaTreat(String code, String val){
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
