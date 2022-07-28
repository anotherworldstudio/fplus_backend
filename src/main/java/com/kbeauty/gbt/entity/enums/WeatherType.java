package com.kbeauty.gbt.entity.enums;

public enum WeatherType  implements CodeVal{
	
	TEMPERATURE("12000", "기온"),
	HUMIDITY("12010", "자외선"),
	CON("12020", "습도"),
	FINEDUST("12030", "미세먼지"),
	SKY("12040", "하늘상태"),
	RAIN("12050", "강수"),
	;
	
	private String code;
	private String val;
	
	WeatherType(String code, String val){
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
