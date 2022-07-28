package com.kbeauty.gbt.entity.enums;

public enum SkinAreaEditor {
	WRINKLE("10400", "주름"),
	PORE("10410", "모공"),
	TROUBLE("10420","Trouble"),
	PIGMENT("10430","Pigment"),
	REDNESS("10440","Redness")
	;
		
	private String code;	
	private String val;
	
	SkinAreaEditor(String code, String val){
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
