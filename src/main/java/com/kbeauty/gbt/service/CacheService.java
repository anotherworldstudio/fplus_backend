package com.kbeauty.gbt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.kbeauty.gbt.entity.enums.CacheKey;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheService {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	private void put(String key, String val) {
//		log.info("========CacheService.put========");
//		log.info(key + " : " + val);
//		log.info("=========================");
//		ValueOperations<String, String> vop = redisTemplate.opsForValue();
//		vop.set(key, val);
	}
	
	private String get(String key) {
		return null;
//		log.info("========CacheService.get========");
//		log.info(key);
//		log.info("=========================");
//		ValueOperations<String, String> vop = redisTemplate.opsForValue();
//		return vop.get(key);
	}
	
	private void del(String key) {
//		log.info("========CacheService.del========");
//		log.info(key);
//		log.info("=========================");
//		redisTemplate.delete(key);
	}
	
	private String getCacheKey(CacheKey type, String key) {
		StringBuffer sb = new StringBuffer();
		return sb.append(type.getCacheKey()).append("_").append(key).toString();
	}	
	
	public String get(CacheKey type, String key) {
		String cacheKey = getCacheKey(type, key);
		return get(cacheKey);
	}
	
	public void put(CacheKey type, String key, String val) {
		String cacheKey = getCacheKey(type, key);
		put(cacheKey, val);
	}
	
	public void del(CacheKey type, String key) {
		String cacheKey = getCacheKey(type, key);
		del(cacheKey);
	}
	
}
