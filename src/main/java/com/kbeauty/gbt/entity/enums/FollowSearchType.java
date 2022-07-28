package com.kbeauty.gbt.entity.enums;

public enum FollowSearchType implements CodeVal{
	BLOCK("0", "BLOCK"),
	FOLLOW("1", "Follow"),
	FOLLOWING("2", "FOLLOWING")
	;
		
	private String code;
	private String val;
	
	FollowSearchType(String code, String val){
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
