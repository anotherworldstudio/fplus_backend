package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;
import lombok.Data;

@Data
public class RecruitByCondition extends Condition {

	private String userid;
	private String premiumYn;
	private String premiumStart;
	private String premiumEnd;
	private String userName;


	public boolean isEmpty() {
		return premiumYn == null || premiumYn.isEmpty();
	}
}
