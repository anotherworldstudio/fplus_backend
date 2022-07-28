package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class AiRecommandProd  extends CommonView{
	String groupId;
	String itemCode;
	int fromVal;
	int toVal;
	String refId;
	String contentType;
	String content;
}
