package com.kbeauty.gbt.entity.enums;

public enum Sex {
	
	FEMAIL("F"),MAIL("M");
	
	private String val;	
	
	Sex(String val){
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}

}
