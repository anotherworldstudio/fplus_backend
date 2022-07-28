package com.kbeauty.gbt.entity.enums;

public enum LikeType implements CodeVal{
	VIEW("0000", "조회"),
	LIKE("1000", "좋아요"),
	FAV("2000", "즐겨찾기"),
	REPLY("3000", "댓글")
	;
		
	private String code;
	private String val;
	
	LikeType(String code, String val){
		this.code = code;
		this.val = val;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setLikeType(LikeType likeType) {
		this.code = likeType.getCode();
		this.val = likeType.getVal();
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
	
	public LikeType getEnum(String code) {
		for (LikeType likeType : LikeType.values()) { 
		     if(likeType.getCode().equals(code)) {
		    	 return likeType;
		     }
		}
		return null;
	}
}
