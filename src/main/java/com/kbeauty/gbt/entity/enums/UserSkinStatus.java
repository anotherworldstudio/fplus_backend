package com.kbeauty.gbt.entity.enums;

public enum UserSkinStatus implements CodeVal{
	REG("0000", "Register"),
	REQ("1000", "Request"),  // 진단 대기 
	COM("2000", "Complete"), //  진단 완료 
	DIS("9000", "Dismiss"),  // 진단 불가 
	;
		
	private String code;
	private String val;
	
	UserSkinStatus(String code, String val){
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
