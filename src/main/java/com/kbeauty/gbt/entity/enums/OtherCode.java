package com.kbeauty.gbt.entity.enums;

public enum OtherCode implements CodeVal{
	
	
	WHITENING       ("FT_1000", "미백"),
	WRINKLECARE     ("FT_1010", "주름개선"),
	SUNCARE         ("FT_1020", "자외선차단"),
	HAIRCARE        ("FT_1030", "탈모완화"),
	VEGAN           ("FT_1040", "비건"),
	CRUELTYFREE     ("FT_1050", "크루얼티프리"),
	HYPOALLERGENIC  ("FT_1060", "저자극"),
	NONCOMEDOGENIC  ("FT_1070", "논코메도제닉")
	;	
		
	private String code;
	private String val;
	
	OtherCode(String code, String val){
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
