package com.kbeauty.gbt.service;

import com.kbeauty.gbt.entity.domain.CommonDomain;
import com.kbeauty.gbt.util.CommonUtil;

public class CommonService {
	
	protected<T extends CommonDomain> void setBasicInfo(T t, String userId) {
		String dateStr = CommonUtil.getSysTime();		
		t.setBasicInfo(dateStr, userId);
	}

}
