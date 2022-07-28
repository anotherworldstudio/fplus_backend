package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class UserSkinCondition extends Condition {

	private long seq;
	private String userId;
	private String dDay;
	private String skinId;
	private String startDate;
	private String endDate;
	private String status;
	private String skinStatus;
	private String doctorId;
	private String searchUserName;
	
	public boolean isEmpty() {
		return  StringUtil.isEmpty(seq) &&
				StringUtil.isEmpty(userId) &&
				StringUtil.isEmpty(dDay) &&
				StringUtil.isEmpty(skinId) &&
				StringUtil.isEmpty(doctorId) &&
				StringUtil.isEmpty(skinStatus);		
	}
	
}
