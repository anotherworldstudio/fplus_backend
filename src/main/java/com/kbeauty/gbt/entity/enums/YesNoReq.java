package com.kbeauty.gbt.entity.enums;

public enum YesNoReq {
	
	YES("Y"),NO("N"),REQ("R"); // 신청 ==> 승인 / 거절 
	
	private String val;
	
	YesNoReq(String val){
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static boolean isYes(String code) {
		return YES.getVal().equals(code);
	}
	
	public static boolean isNo(String code) {
		return NO.getVal().equals(code);
	}
	
	public static boolean isReq(String code) {
		return REQ.getVal().equals(code);
	}
	
}
