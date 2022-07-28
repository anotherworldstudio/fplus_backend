package com.kbeauty.gbt.entity.enums;

public enum AppleLoginStatus implements CodeVal{
	
	
	REG("0000", "등록"),
	CON("2000", "확정");
		
		
	private String code;
	private String val;
	
	AppleLoginStatus(String code, String val){
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
