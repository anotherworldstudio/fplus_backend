package com.kbeauty.gbt.entity.enums;

public enum RecruitActive implements CodeVal{


	ACTIVE("1000", "활성화"),
	PASSIVE("0000", "비활성화"),
	;


	private String code;
	private String val;

	RecruitActive(String code, String val){
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
