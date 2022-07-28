package com.kbeauty.gbt.entity.enums;

public enum FollowStatus implements CodeVal{
	REQ("R", "요청"),
	WDR("W", "요청삭제"),
	CON("C", "승인"),
	UNF("U", "unfollowing"),
	DEL("D", "삭제") 
	;
		
	private String code;
	private String val;
	
	FollowStatus(String code, String val){
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
