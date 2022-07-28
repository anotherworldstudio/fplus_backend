package com.kbeauty.gbt.entity.enums;

public enum UserRole implements CodeVal{
	
	ADMIN("0000", "관리자"),
	HOMEPAGE("0010", "홈페이지 유저"),
	USER("1000", "일반사용자"),
	PARTNER("2000", "뷰티저니 파트너"),
	DOCTOR("3000", "피부진단의사"),
	PROFESSIONAL("4000", "전문가"),
	EDITOR("9000", "에디터");
		
	private String code;
	private String val;
	
	UserRole(String code, String val){
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
