package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;
import lombok.Data;

@Data
public class RecruitCondition extends Condition {

	public RecruitCondition() {
		path = StringUtil.MAX_PATH;
	}

	private String recruitId;
	private String userId;
	private String recruitType;
	private String active;
	private String status;
	private String path;
	private String searchUserid;
	private String searchUserName;

	private long recruitSeq;
	private String title;
	private String content;

	public boolean isEmpty() {
		return recruitType == null && status == null;
	}
}
