package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class LessonCondition extends Condition {
	
	private String lessonId;
	private String lessonType;
	private String ownerId;
	private String status;
	private String viewType;
	private String searchUserid;
	private String searchUserName;

	private String category1;
	private String category2;

	private boolean userFace;
	private String lessonFaceType;
	private String lessonFaceCategory;
	private String age;
	private String facialContour;
	private String seasonColor;
	private String skinTone;
	private String gender;
	
	private long lessonSeq;
	private String title;
	private String active;
	
	private String copyLessonId;
	
	/**
	 * 정렬 조건
	 */
	private String ordersType;
	
	public boolean isEmpty() {
		return status == null;
	}
}
