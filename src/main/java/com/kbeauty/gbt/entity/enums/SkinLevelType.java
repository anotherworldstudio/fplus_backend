package com.kbeauty.gbt.entity.enums;

public enum SkinLevelType implements CodeVal{
	
	MAPPING("0000", "기본메핑"),
	RANDOM_GROUP("1000", "랜덤그룹메핑"),
	BOTTOM("2000", "하위그룹"),
	;
		
	private String code;
	private String val;
	
	SkinLevelType(String code, String val){
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
