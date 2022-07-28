package com.kbeauty.gbt.entity.enums;

public enum ContentViewType implements CodeVal{
	
	ALL("9999", "전체보기")
	,ONLYME("1000", "나만보기")
	,FOLLOW("2000", "Follow만보기")
	,ADMIN("8000", "관리자만보기")
	;
	
	private String code;
	private String val;
	
	ContentViewType(String code, String val){
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
