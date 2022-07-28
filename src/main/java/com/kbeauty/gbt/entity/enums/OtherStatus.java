package com.kbeauty.gbt.entity.enums;

public enum OtherStatus implements CodeVal{
	REG("0000", "Register"),
	DEL("9000", "Delete")
	;
		
	private String code;
	private String val;
	
	OtherStatus(String code, String val){
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
