package com.kbeauty.gbt.entity.enums;

public enum StoragePath implements CodeVal{
	USER("1000", "User"),
	CONTENT_USER("2000", "Content User"),
	CONTENT_USER_DAILY("2100", "Content User Daily"),
	AI_USER("3000", "AI User"),
	AI_USER_DAILY("3100", "AI User Daily"),
	TRAINING("4000", "GBT Training")
	;
		
	private String code;
	private String val;
	
	StoragePath(String code, String val){
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
