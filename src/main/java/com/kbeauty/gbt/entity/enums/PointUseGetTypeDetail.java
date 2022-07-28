package com.kbeauty.gbt.entity.enums;

public enum PointUseGetTypeDetail implements CodeVal{
	// 아래 항목에서 세부적으로 10단위로 쪼갤 것
	
	ADMIN("0000", "관리자 강제권한"),
	ADMIN_DEL("0010", "관리자 삭제"),
	
	EVENT("1000", "이벤트 관련"),
	INVITE("1010","친구 초대"),
	
	GAME("2000", "게임 관련"),
	STAGE_CLEAR("2010","스테이지 클리어"),
	
	
	PAY("3000", "상품 관련"),
	BUY_PRODUCT("3010","상품 구매"),
	REVIEW("3020","상품 리뷰"),
	
	SNS("4000","소셜 관련"),
	FEED("4010","피드 등록"),
	REPLY("4020","댓글 등록"),
	
	;
		
	private String code;
	private String val;
	
	PointUseGetTypeDetail(String code, String val){
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