package com.kbeauty.gbt.entity.enums;

public enum OAuthType implements CodeVal{
	
	BASIC("0000", "기본"),
	KAKAO("1000", "카카오"),
	NAVER("2000", "네이버"),
	GOOGLE("3000", "구글"),
	FACEBOOK("4000", "페이스북"),
	APPLE("5000", "애플");
		
	private String code;
	private String val;
	
	OAuthType(String code, String val){
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
