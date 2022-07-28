package com.kbeauty.gbt.entity.view;	

import java.util.List;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.PointType;
import com.kbeauty.gbt.entity.domain.Weather;

import lombok.Data;

@Data
public class PointView extends CommonView {
	
	private PointType pointType;
	private List<Mileage> mileList;
	private List<Exp> expList;
	
	
	private String recommendUserName; //추천받는 상대 닉네임
	
}
