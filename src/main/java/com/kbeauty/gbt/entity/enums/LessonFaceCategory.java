package com.kbeauty.gbt.entity.enums;

public enum LessonFaceCategory implements CodeVal{
	
	MY("0000","자기 자신정보"),
	OTHERS("1000","딴사람 정보");
		
	private String code;
	private String val;
	
	LessonFaceCategory(String code, String val){
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
