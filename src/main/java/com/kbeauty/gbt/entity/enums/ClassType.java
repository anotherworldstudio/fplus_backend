package com.kbeauty.gbt.entity.enums;

public enum ClassType implements CodeVal{
	
	
	PRODUCT("1000", "PRODUCT"),
	POINT("2000", "POINT")
	;		
		
	private String code;
	private String val;
	
	ClassType(String code, String val){
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
