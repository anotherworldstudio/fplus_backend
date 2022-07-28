package com.kbeauty.gbt.entity.enums;

public enum PriceType implements CodeVal{
	
	PRODUCT("1000", "Product")
	;
		
	private String code;
	private String val;
	
	PriceType(String code, String val){
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
