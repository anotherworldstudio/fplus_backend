package com.kbeauty.gbt.entity.view;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class WeatherCondition extends Condition {
	
	public WeatherCondition() {
		
	}
	
	private long seq;	
	private String weatherId;
	private String weatherType;
	private String weatherCode;
	private String weatherName;
	private String searchUserid;
	private String productName;
	private String weatherMsg;
	
	/**
	 * 정렬 조건
	 */
	private boolean gradeOrder;
	private boolean seqOrder;
	
	
	
	
	public boolean isEmpty() {
		return  StringUtil.isEmpty(seq) &&
				StringUtil.isEmpty(weatherType) &&
				StringUtil.isEmpty(weatherId) &&
				StringUtil.isEmpty(weatherCode) &&
				StringUtil.isEmpty(weatherName);		
	}
	
}
