package com.kbeauty.gbt.entity.enums;

public enum ContentActive implements CodeVal{
	
	
	ACTIVE("1000", "Active"),
	PASSIVE("0000", "Passive"),
	;
		
		
	private String code;
	private String val;
	
	ContentActive(String code, String val){
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
