package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class FollowCondition extends Condition {

	private String followId    ;
	private String userName    ;
	private String followType  ;
	private String reqType     ;
	private String resType     ;
	private String userId      ;
	private String followerId  ;
	private String searchType  ; // 1 : 팔로워(나를 따르라) / 2: 팔로잉 (내가 따르리) / 0 : Block 
	
	public boolean isEmpty() {
		return StringUtil.isEmpty(followId) && 
				StringUtil.isEmpty(userName) &&				
				StringUtil.isEmpty(followType) &&
				StringUtil.isEmpty(reqType) &&
				StringUtil.isEmpty(resType) &&
				StringUtil.isEmpty(userId) &&
				StringUtil.isEmpty(followerId);
	}
}
