package com.kbeauty.gbt.entity.enums;

public enum SkinTone implements CodeVal {
	NONE("9999", "NONE"),
	WARM("0000", "웜톤"),
	COOL("1000", "쿨톤"),
	NATURAL("2000","네츄럴")
	;
		
	private String code;	
	private String val;
	
	SkinTone(String code, String val){
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
