package com.kbeauty.gbt.entity.enums;

public enum SkinTestStepType implements CodeVal{
	
	OD("0000", "지성 건성"),
	SR("1000", "민감성 저항성"),
	PN("2000", "색소 비색소"),
	WT("3000", "주름 탄력");
		
	private String code;
	private String val;
	
	SkinTestStepType(String code, String val){
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
