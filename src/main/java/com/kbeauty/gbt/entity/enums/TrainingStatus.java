package com.kbeauty.gbt.entity.enums;

public enum TrainingStatus implements CodeVal{
	
	ONE("0000", "1차 등록"),
	TWO("1000", "2차 등록"),
	CON("2000", "심사완료"),
	DEL("9000", "삭제");
		
	private String code;
	private String val;
	
	TrainingStatus(String code, String val){
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
