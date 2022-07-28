package com.kbeauty.gbt.entity.enums;

public enum Grade implements CodeVal{
	NONE("9999", "NONE"),
	GRADE1("1000", "A+"),
	GRADE2("2000", "A"),
	GRADE3("3000", "A-"),
	GRADE4("4000", "B+"),
	GRADE5("5000", "B"),
	GRADE6("6000", "B-"),
	GRADE7("7000", "C+"),
	GRADE8("8000", "C"),
	GRADE9("9000", "C-"),
	GRADE10("0000", "D")
	;
		
	private String code;
	private String val;
	
	Grade(String code, String val){
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
