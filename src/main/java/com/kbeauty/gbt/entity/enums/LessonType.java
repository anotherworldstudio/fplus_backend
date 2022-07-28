package com.kbeauty.gbt.entity.enums;

public enum LessonType implements CodeVal{
	INNOCENCE("0000", "청순 꾸안꾸"),
	HIP("1000", "힙한"),
	ATMOSPHERIC("2000", "분위기있는"),
	LOVELY("3000", "사랑스러운"),
	FRUITY ("4000", "과즙상"),
	GRACEFUL("5000", "단아한"),
	MELLOW("6000", "그윽한"),
	CHIC("7000", "시크한"),
	LUXURIOUS("8000", "고급스러운"),
	ELEGANT("9000", "우아한")
	;
		
	private String code;
	private String val;
	
	LessonType(String code, String val){
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
