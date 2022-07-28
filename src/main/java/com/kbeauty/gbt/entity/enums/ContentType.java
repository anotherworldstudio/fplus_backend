package com.kbeauty.gbt.entity.enums;

public enum ContentType implements CodeVal{
	FEED("1000", "Feed"),
	FEED_HEADER("1010", "Feed 메인"),
	EVENT("2000", "Event"),
	EVENT_HEADER("2010", "Event 메인"),
	PRODUCT("3000", "제품"),
	PRODUCT_VS("3010", "제품비교"),
	SELF_MANAGE("8000", "에디터픽"),
	BEAUTY_FILM("8010", "뷰티 크리에이터 필름"),
	EXPERT_TIP("8020", "전문가 팁"),
	NOTICE("9000", "공지"),
	BOARD("9010", "문의사항"),
	AI_DOCTOR("9020", "AI의사평가"),
	AI_PRODUCT("9030", "AI제품추천"),
	AI_TREAT("9040", "AI시술추천"),
	AI_EXPERT_TIP("9050", "AI전문가 팁"),
	AI_EVENT("9060", "AI EVENT"),
	AI_AGE_CELEB("9070", "AI 같은나이스타"),
	;
		
	private String code;
	private String val;
	
	ContentType(String code, String val){
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
