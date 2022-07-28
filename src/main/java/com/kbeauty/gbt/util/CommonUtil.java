package com.kbeauty.gbt.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import javax.servlet.http.HttpServletRequest;

import com.kbeauty.gbt.entity.enums.CodeVal;

public class CommonUtil {
	
	public static String getGuid() {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		return uuid;
	}
	
	public static int getRandomInt(int maxData) { // Max Data 미포함 / 0 포함  
		Random rand = new Random();
		int nextInt = rand.nextInt(maxData); // 3 ==> 0 1 2
		return nextInt;
	}
	
	public static int getRandomInt(int maxData, int seed) { // Max Data 미포함 / 0 포함  
		Random rand = new Random(seed); 
		int nextInt = rand.nextInt(maxData); // 3 ==> 0 1 2
		return nextInt;
	}
	
	public static int getRandomInt(int maxData, String seed) { // Max Data 미포함 / 0 포함  
		return getRandomInt(maxData, Integer.parseInt(seed));
	}
	
	public static String getIp(HttpServletRequest request) {
		 
        String ip = request.getHeader("X-Forwarded-For");
 
        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP"); 
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // 웹로직            
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");            
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");            
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        
        if("0:0:0:0:0:0:0:1".equals(ip)) {
        	ip = "127.0.0.1";
        }
 
        return ip;
    }
	
	public static String getSysTime() {
		return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMddHH:mm:ss.SSS"));
	}
	
	public static String getDate() {
		return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	}
	
	public static <E extends Enum<E> & CodeVal> String getValue(E[] values, String code) {    	
        for (E e : values) {
        	if (e.getCode().equals(code)) {        		
        		return e.getVal();
        	}
        }
        return "";
    }
	
	public static void sort(int limit, double...ds ) {	
		boolean isTrue = false;
		DoubleStream builderStream =  Arrays.stream(ds);
		Set<Double> collect = builderStream.sorted().limit(limit).boxed().collect(Collectors.toSet());
		//collect.contains(o);
	}
	
	public static void main(String[] args) {
		CommonUtil.sort(2, 4d, 5d, 6d, 7d, 2d);
	}
	

}
