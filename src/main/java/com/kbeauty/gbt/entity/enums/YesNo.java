package com.kbeauty.gbt.entity.enums;

public enum YesNo {
	
	YES("Y"),NO("N");
	
	private String val;
	
	YesNo(String val){
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
	
	public static boolean isYes(String code) {
		return YES.getVal().equals(code);
	}
	
	/**
	 * null 값을 N으로 셋팅하는 함수  
	 * @param yn
	 * @return
	 */
	public static String setNo(String yn) {
		return YES.getVal().equals(yn) ? "Y" : "N";
	}
	
	public static String getYN(boolean b) {
		return b?"Y":"N";
	}
}
