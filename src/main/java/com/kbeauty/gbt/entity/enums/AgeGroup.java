package com.kbeauty.gbt.entity.enums;

public enum AgeGroup implements CodeVal{
	NONE("9999", "NONE"),
	Teenage("1000", "10대"),
	TWENTIES ("2000", "20대"),
	THIRTIES("3000", "30대"),
	FORTYS("4000", "40대"),
	FIFTYS("5000", "50대")
	;
		
	private String code;
	private String val;
	
	AgeGroup(String code, String val){
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
