package com.kbeauty.gbt.entity.enums;

public enum Currency implements CodeVal{
	
	KRW("KRW", "원"),
	USD("USD", "달라")
	;
		
	private String code;
	private String val;
	
	Currency(String code, String val){
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
