package com.kbeauty.gbt.entity.enums;

public enum SkinTestType implements CodeVal{
	
	// MBTI of SKIN  
	ENFP("2000", "색소 비색소"),
	INFJ("3000", "주름 탄력");
		
	private String code;
	private String val;
	
	SkinTestType(String code, String val){
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
