package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;
import lombok.Data;

@Data
public class UserCondition2 extends Condition {
	private String userId      ;
	private String password    ;
	private String email;
	private String userRole;
	private String friendId;


	public boolean isEmpty() {
		return StringUtil.isEmpty(userId) &&
				StringUtil.isEmpty(password) &&
				StringUtil.isEmpty(userRole);
	}

}
