package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.util.StringUtil;
import lombok.Data;

@Data
public class UserCondition2 extends Condition {
	private String userId      ;
	private String name;
	private String password    ;
	private String email;
	private String userRole;
	private String friendId;
	private String place;
	private String sex;
	private String birth;
	private String status;


	public boolean isEmpty() {
		return StringUtil.isEmpty(userId) &&
				StringUtil.isEmpty(password) &&
				StringUtil.isEmpty(userRole);
	}

}
