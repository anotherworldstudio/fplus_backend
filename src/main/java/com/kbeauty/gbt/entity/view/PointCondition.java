package com.kbeauty.gbt.entity.view;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class PointCondition extends Condition {
	
	public PointCondition() {
		
	}
	
	private long seq;	
	private String pointTypeId;
	private String userId;
	private String bigType;
	private String systemType;
	private String typeName;
	private String mileType;
	private String plusMinus;
	private int mile;
	private String searchUserid;
	private Date searchStartDate;
	private Date searchEndDate;
	private String startDate;
	private String endDate;
	private String kindType;
	private String deleteId;
	
	/**
	 * 정렬 조건
	 */
	private boolean gradeOrder;
	private boolean seqOrder;
	private String ordersType;
	
	
	public boolean isEmpty() {
		return  StringUtil.isEmpty(seq) &&
				StringUtil.isEmpty(bigType) &&
				StringUtil.isEmpty(userId) &&
				StringUtil.isEmpty(plusMinus) &&
				StringUtil.isEmpty(searchUserid);		
	}
	
}
