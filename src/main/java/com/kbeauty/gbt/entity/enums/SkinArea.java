package com.kbeauty.gbt.entity.enums;

public enum SkinArea {
//	WRINKLE("10000", "10120", "주름"),
//	PORE("10010", "10130", "모공"),
//	TROUBLE("10020", "10140", "Trouble"),
//	PIGMENT("10030", "10150", "Pigment"),
//	REDNESS("10040", "10160", "Redness")
	
	WRINKLE("10000",  "주름"),
	PORE("10010",  "모공"),
	TROUBLE("10020", "Trouble"),
	PIGMENT("10030", "Pigment"),
	REDNESS("10040", "Redness")
	;
		
	private String code;
//	private String appendCode;
	private String val;
	
	SkinArea(String code, 
			// String appendCode, 
			String val){
		
		this.code = code;
		this.val = val;
//		this.appendCode = appendCode;
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
	
	public SkinAreaProd getSkinAreaProd() {
		SkinAreaProd[] values = SkinAreaProd.values();
		for (SkinAreaProd skinAreaProd : values) {
			if(skinAreaProd.name().equals(this.name())) {
				return skinAreaProd;
			}
		}
		return null;
	}
	
	public SkinAreaTreat getSkinAreaTreat() {
		SkinAreaTreat[] values = SkinAreaTreat.values();
		for (SkinAreaTreat skinAreaTreat : values) {
			if(skinAreaTreat.name().equals(this.name())) {
				return skinAreaTreat;
			}
		}
		return null;
	}
	
	public SkinAreaEditor getSkinAreaEditor() {
		SkinAreaEditor[] values = SkinAreaEditor.values();
		for (SkinAreaEditor skinAreaTreat : values) {
			if(skinAreaTreat.name().equals(this.name())) {
				return skinAreaTreat;
			}
		}
		return null;
	}

}
