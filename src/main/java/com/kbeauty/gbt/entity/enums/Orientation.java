package com.kbeauty.gbt.entity.enums;

public enum Orientation{
	//1. 0도, 3. 180도, 6. 270도, 8. 90도 회전한 정보
	R000(1, "정상 기울기"),
	R090(6, "우측으로 90"),
	R180(3, "우측으로 180"),
	R270(8, "우측으로 270"),
	;
	 
	private int code;
	private String val;
	
	Orientation(int code, String val){
		this.code = code;
		this.val = val;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
	
	public Orientation getOrientation(int orientation) {
		Orientation[] values = values();
		for (Orientation ori : values) {
			if(ori.getCode() == orientation) {
				return ori; 
			}
		}
		return R000;
	}
}
