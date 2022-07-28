package com.kbeauty.gbt.entity.enums;

public enum ItemType implements CodeVal{
	SKIN("1000", "SKIN_INFO"),
	PATTERN("2000", "PATTERN")
	;
		
	private String code;
	private String val;
	
	ItemType(String code, String val){
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
