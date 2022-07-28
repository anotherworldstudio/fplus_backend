package com.kbeauty.gbt.entity.enums;

public enum Gender implements CodeVal{
	NONE("9999", "NONE"),
	MEN("0000", "남성"),
	WOMEN("1000", "여성"),
	ANOTHER("2000", "제3의성")
	;
		
	private String code;
	private String val;
	
	Gender(String code, String val){
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
