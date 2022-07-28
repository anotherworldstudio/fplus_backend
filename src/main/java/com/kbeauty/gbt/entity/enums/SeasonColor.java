package com.kbeauty.gbt.entity.enums;

public enum SeasonColor implements CodeVal{
	NONE("9999", "NONE"),
	
	SPRING("1000", "SPRING"),
	SPRING_TRUE("1100", "SPRING_TRUE"),
	SPRING_LIGHT("1200", "SPRING_LIGHT"),
	SPRING_BRIGHT("1300", "SPRING_BRIGHT"),
	
	SUMMER("2000", "SUMMER"),
	SUMMER_TRUE("2100", "SUMMER_TRUE"),
	SUMMER_DEEP("2200", "SUMMER_LIGHT"),
	SUMMER_SOFT("2300", "SUMMER_SOFT"),
	
	AUTUMN("3000", "AUTUMN"),
	AUTUMN_TRUE("3100", "AUTUMN_TRUE"),
	AUTUMN_LIGHT("3200", "AUTUMN_DEEP"),
	AUTUMN_SOFT("3300", "AUTUMN_SOFT"),
	
	WINTER("4000", "WINTER"),
	WINTER_TRUE("4100", "WINTER_TRUE"),
	WINTER_DEEP("4200", "WINTER_DEEP"),
	WINTER_BRIGHT("4300", "WINTER_BRIGHT")
	;
		
	
      
      
	private String code;
	private String val;
	
	SeasonColor(String code, String val){
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
