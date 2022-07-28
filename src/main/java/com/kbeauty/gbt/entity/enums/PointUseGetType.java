package com.kbeauty.gbt.entity.enums;

public enum PointUseGetType implements CodeVal{
	// 아래 항목에서 세부적으로 10단위로 쪼갤 것
	
	ADMIN("0000", "관리자 강제권한"),
	
	EVENT("1000", "이벤트 관련"),
	
	GAME("2000", "게임 관련"),
	
	PAY("3000", "상품 관련"),
	
	SNS("4000","소셜 관련"),
	
	;
		
	private String code;
	private String val;
	
	PointUseGetType(String code, String val){
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