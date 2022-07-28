package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.Condition;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class ContentCondition extends Condition {
	
	public ContentCondition() {
		path = StringUtil.MAX_PATH;
	}

	private String active;
	private String contentId;
	private String consumerId;
	private String contentType;
	private String ownerId;
	private String replyYn;
	private String status;
	private String viewType;
	private String path;
	private String searchUserid;
	private String searchUserName;

	private String category1;
	private String category2;
	private String category3;
	
	private String vendor;

	private long contentSeq;
	private String title;
	
	
	/**
	 * 정렬 조건
	 */
	private boolean gradeOrder;
	private boolean seqOrder;
	private String ordersType;
	
	public boolean isEmpty() {
		return contentType == null && status == null;
	}
}
