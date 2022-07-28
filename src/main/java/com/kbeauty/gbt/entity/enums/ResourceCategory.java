package com.kbeauty.gbt.entity.enums;

public enum ResourceCategory implements CodeVal{
	NOT("9999", "상관없음"),
	BASE("0000", "베이스"),
	LIB("1000", "립"),
	EYES("2000", "눈"),
	BROW("3000", "눈썹")
	;
		
	private String code;
	private String val;
	
	ResourceCategory(String code, String val){
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
