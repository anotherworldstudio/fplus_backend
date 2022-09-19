package com.kbeauty.gbt.entity.enums;

public enum UserRole2 implements CodeVal{

	USER("0000", "일반사용자"),
	ADMIN("1000", "관리자"),
	PARTNER("2000", "배우");

	private String code;
	private String val;

	UserRole2(String code, String val){
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
