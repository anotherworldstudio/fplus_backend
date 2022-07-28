package com.kbeauty.gbt.entity.enums;

public enum UserStatus  implements CodeVal{
	//Grade
	//type
	//status
	
	REG("0000", "등록"),
	CON("2000", "확정"),
	DEL("9000", "삭제");
	
	private String code;
	private String val;
	
	UserStatus(String code, String val){
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
