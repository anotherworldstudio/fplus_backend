package com.kbeauty.gbt.entity.enums;

public enum Checked {
	
	YES("1"),NO("0");
	
	private String val;
	
	Checked(String val){
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
}
