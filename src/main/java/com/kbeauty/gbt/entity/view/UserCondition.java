package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class UserCondition extends Condition {

	private String cellphone   ;
	private String userName    ;
	private String email       ;
	private String status      ;
	private String marketingYn ;
	private String userId      ;
	private String userRole    ;
	
	public boolean isEmpty() {
		return StringUtil.isEmpty(cellphone) && 
				StringUtil.isEmpty(userName) &&
				StringUtil.isEmpty(email) &&
				StringUtil.isEmpty(status) &&
				StringUtil.isEmpty(marketingYn) &&
				StringUtil.isEmpty(userId) &&
				StringUtil.isEmpty(userRole);
	}

}
