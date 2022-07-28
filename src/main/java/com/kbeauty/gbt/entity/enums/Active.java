package com.kbeauty.gbt.entity.enums;

public enum Active implements CodeVal{
	ACTIVE("9999", "Active"),
	PASSIVE("0000", "PASSIVE")
	;
		
	private String code;
	private String val;
	
	Active(String code, String val){
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
