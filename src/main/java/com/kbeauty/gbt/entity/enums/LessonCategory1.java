package com.kbeauty.gbt.entity.enums;

public enum LessonCategory1 implements CodeVal{
	ALL("0000", "전체영상"),
	NOTALL("1000", "부분영상")
	;
		
	private String code;
	private String val;
	
	LessonCategory1(String code, String val){
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
