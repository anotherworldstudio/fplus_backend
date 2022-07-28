package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class AiRecommandRaw  extends CommonView{
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
	
	public AiRecommand genAiRecommand() {
		AiRecommand result = new AiRecommand();
		result.setGroupId(groupId);
		result.setItemCode(itemCode);
		result.setFromVal(fromVal);
		result.setToVal(toVal);
		result.setRefId(refId);
		result.setContentType(contentType);
		result.setContent(content);
		result.setRefGroup(refGroup);
		result.setLevelType(levelType);
		result.setPickCount(pickCount);		
		return result;
	}
}


