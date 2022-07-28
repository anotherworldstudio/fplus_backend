package com.kbeauty.gbt.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static String lapd0(String str, int size) {
		return lapd(str, size, "0");
	}
	
	public static String lapd0(long str, int size) {		
		return lapd(String.valueOf(str), size, "0");
	}
	
	public static String lapd0(int str, int size) {		
		return lapd(String.valueOf(str), size, "0");
	}
	
	public static String lapd9(String str, int size) {
		return lapd(str, size, "9");
	}
	
	public static String lapd9(long str, int size) {		
		return lapd(String.valueOf(str), size, "9");
	}
	
	public static String lapd9(int str, int size) {		
		return lapd(String.valueOf(str), size, "9");
	}
	
	public static String lapd(String str, int size, String strChar) {
		if(StringUtil.isEmpty(str)) return str;
		if(str.length() >= size) return str;
		StringBuilder sb = new StringBuilder();
		for (int i = str.length(); i < size; i++) {
			sb.append(strChar);
		}
		sb.append(str);
		
		return sb.toString();
	}
	
	public static boolean isEmpty(String str) {
		return str == null || "".equals(str);
	}
	
	public static boolean isEmpty(Long seq) {
		return seq == 0;
	}
	
	public static boolean isEmpty(Integer seq) {
		if(seq == null) return true;
		return seq == 0;
	}
	
	public static boolean isEmpty(List<?> list) {
		return list == null || list.isEmpty();
	}
	
	public static boolean isEmpty(Set<?> set) {
		return set == null || set.isEmpty();
	}
	
	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}
	
	
	public static void main(String[] args) {
//		String str = null;
//		for(int i = 0 ; i < 11 ; i++) {			
//			str = StringUtil.lapd0("1000", i);
//			System.out.println(str);
//		}
//		
//		str = StringUtil.lapd0("1000", 3);
//		System.out.println(str);
//		
//		str = StringUtil.lapd0("1000", 6);
//		System.out.println(str);
		String upperPath = "000000000009-999999999999-999999999999-999999999999-999999999999-999999999999";
		
		String str = upperPath.substring(0,13); // 1 depth 
		str = upperPath.substring(0,26); // 2 
		str = upperPath.substring(0,39); // 3
		str = upperPath.substring(0,52); // 4
		str = upperPath.substring(0,65); // 5 
		str = upperPath.substring(0,77); // 6
		
		getPath(10, upperPath, 2);
	}

	/**
	 * 999999999999-999999999999-999999999999-999999999999-999999999999-999999999999
	 * 5 depth 가능 , 1조 게시물 가능 
	 * @param seq
	 * @param upperPath
	 * @return
	 */
	
	public final static String MAX_PATH = "999999999999-999999999999-999999999999-999999999999-999999999999-999999999999";
	
	/*
	 * 기존에 path를 이용해서 부모의 path 정보를 조합한다.
	 */
	public static String getParentPath(String path) {
		StringBuffer sb = new StringBuffer();
		sb.append(path.substring(0, 12));
		sb.append("-999999999999-999999999999-999999999999-999999999999-999999999999");
		return sb.toString();
	}
	
	public static String getRelpyPath(long seq) {
		return lapd0(seq, 12);
	}
	
	public static String getPath(long seq, String upperPath, int depth) {
		
		StringBuffer sb = new StringBuffer();
		if (depth > 5)
			depth = 5;
		switch (depth) {
		case 0:
			sb.append(lapd0(seq, 12));
			sb.append("-999999999999-999999999999-999999999999-999999999999-999999999999");
			break;
		case 1:
			sb.append(upperPath.substring(0,13));
			sb.append(lapd0(seq, 12));
			sb.append("-999999999999-999999999999-999999999999-999999999999");
			break;
		case 2:
			sb.append(upperPath.substring(0,26));
			sb.append(lapd0(seq, 12));
			sb.append("-999999999999-999999999999-999999999999");
			break;
		case 3:
			sb.append(upperPath.substring(0,39));
			sb.append(lapd0(seq, 12));
			sb.append("-999999999999-999999999999");
			break;
		case 4:
			sb.append(upperPath.substring(0,52));
			sb.append(lapd0(seq, 12));
			sb.append("-999999999999");
			break;
		case 5:
			sb.append(upperPath.substring(0,65));
			sb.append(lapd0(seq, 12));
			break;
		default:
			break;
		}
		return sb.toString();
	}
	
	public static boolean isUrl(String text) {
		if(StringUtil.isEmpty(text)) return false;
		
		if(text.startsWith("http")) {
			return true;
		}else {
			return false;
		}
		
	}
	public static String removeSpecialChar(String str) {
		return str.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
	}
	

}
