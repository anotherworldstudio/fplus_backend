package com.kbeauty.gbt.entity.enums;

public enum TableToClass implements CodeVal{
	CONTENT("CONTENT", "com.kbeauty.gbt.entity.domain.Content"),
	PRICE("PRICE", "com.kbeauty.gbt.entity.domain.Price"),
	USER("USER", "com.kbeauty.gbt.entity.domain.User"),
	SKINLEVEL("SKINLEVEL", "com.kbeauty.gbt.entity.domain.SkinLevel"),
	SKINITEM("SKINITEM", "com.kbeauty.gbt.entity.domain.SkinItem"),
	;
		
	private String code;
	private String val;
	
	TableToClass(String code, String val){
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
