package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class TrainingCondition extends Condition {
	
	private String trainingId;
	private String lessonId;
	private String userId;
	private String status;
	private String searchUserid;
	private String searchUserName;

	private String category1;
	private String category2;

	private String age;
	private String facialContour;
	private String seasonColor;
	private String skinTone;
	private String gender;
	
	private long trainingSeq;
	
	/**
	 * 정렬 조건
	 */
	private String ordersType;
	
	public boolean isEmpty() {
		return status == null;
	}
}
