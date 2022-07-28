package com.kbeauty.gbt.entity.enums;

public enum OrdersType implements CodeVal{
    BASIC("0000", "basic"),
    SEQ_ASC("1000", "seq ASC"),
	ORDERS_ASC("2000", "orders asc")
	;
		
	private String code;
	private String val;
	
	OrdersType(String code, String val){
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
