package com.kbeauty.gbt.entity.enums;

public enum CacheKey {
	
	USER_EMAIL("EMAIL"),USER_ID("USER_ID");
	
	private String cacheKey;
	
	CacheKey(String cacheKey){
		this.cacheKey = cacheKey;
	}
	
	public String getCacheKey() {
		return cacheKey;
	}

}
