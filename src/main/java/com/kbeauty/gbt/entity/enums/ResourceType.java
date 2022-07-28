package com.kbeauty.gbt.entity.enums;

import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.StringUtil;

public enum ResourceType implements CodeVal{
	IMAGE("1000", "미디어 이미지"),
	VIDEO("1010", "미디어 동영상"),	
	UTUBE("1020", "YouTubeUrl"),
	AUDIO("1030", "미디어 음성"),
	PRODUCT("1040", "상품순번"),
	TEXT("1050", "텍스트"),
	HASHTAG("2000", "해쉬태그");
	
		
	private String code;
	private String val;	
	
	ResourceType(String code, String val){
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
	
	public static ResourceType getCode(MultipartFile file) {
		
		String contentType = file.getContentType();
		if(StringUtil.isEmpty(contentType)) {
			throw new MessageException(ErrMsg.CONTENT_TYPE_ERR);
		}
		
		String[] contTypeArr = contentType.split("/");
		
		if("image".equalsIgnoreCase(contTypeArr[0])) {
			return IMAGE;
		}else if("audio".equalsIgnoreCase(contTypeArr[0])) {
			return AUDIO;
		}else if("video".equalsIgnoreCase(contTypeArr[0])) {
			return VIDEO;
		}else {
			throw new MessageException(ErrMsg.CONTENT_TYPE_ERR);
		}
	}
	
	public static boolean isStorageUrl(String code) {
		return IMAGE.getCode().equals(code) || VIDEO.getCode().equals(code) || AUDIO.getCode().equals(code);
	}
	
	public static boolean isUTubeUrl(String code) {
		return UTUBE.getCode().equals(code) ;
	}
	
	public static boolean isProduct(String code) {
		return PRODUCT.getCode().equals(code) ;
	}
	public static boolean isText(String code) {
		return TEXT.getCode().equals(code) ;
	}
}
