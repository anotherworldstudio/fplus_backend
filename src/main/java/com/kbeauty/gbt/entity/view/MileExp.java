package com.kbeauty.gbt.entity.view;	

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.PointType;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.PointUseGetType;
import com.kbeauty.gbt.entity.enums.PointUseGetTypeDetail;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class MileExp extends CommonView {
	
	private long seq                ;	
	private String userId;
	private String bigType;
	private String systemType;
	private String plusMinus;
	private int point;
	private int oldTotal;
	private int newTotal;
	
	private String bigTypeName;
	private String systemTypeName;
	@Transient
	private String userName;
	
	public void setTypeName() {
		bigTypeName = CommonUtil.getValue(PointUseGetType.values(), bigType);
		systemTypeName = CommonUtil.getValue(PointUseGetTypeDetail.values(), systemType);
	}
	
	public boolean isEmpty() {
		return  
				StringUtil.isEmpty(userId) ||
				StringUtil.isEmpty(systemType);
	}
		
	

}
