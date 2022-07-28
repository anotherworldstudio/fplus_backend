package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class AiRecommand  extends CommonView{
	String groupId;
	String itemCode;
	int fromVal;
	int toVal;
	String refId;
	String contentType;
	String content;
	
	String refGroup;
	String levelType;
	int pickCount;
}
