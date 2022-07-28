package com.kbeauty.gbt.entity.enums;

public enum NoticeType implements CodeVal{
	
	EMERGENCY("0001", "긴급 공지"),
	UPDATE("0002", "앱 업데이트"),
	FEED_NOTICE("1000", "공지 리스트"),
	MAIN_POPUP("2000", "Main POPUP"),
	SKIN_POPUP("2010","Ai 피부분석 POPUP"),
	MYPAGE_POPUP("2020","MyPage POPUP"),
	
	
	;
		
	private String code;
	private String val;
	
	NoticeType(String code, String val){
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
