package com.kbeauty.gbt.entity.enums;

public enum LessonFaceType implements CodeVal{
	AGE("0000","나이"),
	FACIALCONTOUR("1000","얼굴윤곽"),
	SEASONCOLOR("2000","계절컬러"),
	SKINTONE("3000","피부톤"),
	GENDER("4000","성별"),
	TYPE("5000","메이크업타입");
		
	private String code;
	private String val;
	
	LessonFaceType(String code, String val){
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
