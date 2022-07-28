package com.kbeauty.gbt.entity.enums;

public enum FollowType implements CodeVal{
	FOLLOW("0000", "Follow"),
	BLOCK("1000", "Block")
	;
		
	private String code;
	private String val;
	
	FollowType(String code, String val){
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
