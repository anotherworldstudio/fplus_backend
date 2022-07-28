package com.kbeauty.gbt.entity.enums;

public enum OtherType implements CodeVal{
	
	
	FEATURE("1000", "특장점"),
	PEER_PRODUCT("2000", "유사제품"),
	PRODUCT_IMG("3000", "제품이미지"),
	;
		
		
	private String code;
	private String val;
	
	OtherType(String code, String val){
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
