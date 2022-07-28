package com.kbeauty.gbt.entity.enums;

public enum FacialContour implements CodeVal{
	
	NONE("9999", "NONE"),
	EGG("0000", "계란형"),
	ROUND("1000", "둥근형"),
	SQUARE("2000", "사각형"),
	RECTANGLE("3000", "직사각형"),
	HEART("4000", "하트형")
	;
		
	private String code;
	private String val;
	
	FacialContour(String code, String val){
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
