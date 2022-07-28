package com.kbeauty.gbt.entity.enums;

public enum UserAccountType implements CodeVal{
	OPEN("O", "Open"),
	CLOSE("C", "Close")
	;
		
	private String code;
	private String val;
	
	UserAccountType(String code, String val){
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
	
	public String getVal(String code) {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
	
	
	
}
