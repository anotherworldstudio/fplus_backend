package com.kbeauty.gbt.entity.enums;

public enum SkinAreaProd {
	WRINKLE("10200", "주름"),
	PORE("10210", "모공"),
	TROUBLE("10220","Trouble"),
	PIGMENT("10230","Pigment"),
	REDNESS("10240","Redness")
	;
		
	private String code;	
	private String val;
	
	SkinAreaProd(String code, String val){
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
