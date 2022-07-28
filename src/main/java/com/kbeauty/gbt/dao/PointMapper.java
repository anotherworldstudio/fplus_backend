package com.kbeauty.gbt.dao;

import java.util.List;
import java.util.Map;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.GameData;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.PointType;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ClassView;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.MileExp;
import com.kbeauty.gbt.entity.view.PointCondition;
import com.kbeauty.gbt.entity.view.WeatherCondition;

public interface PointMapper {	

	public List<Mileage> getMileageListByUserId(String userId);
	public List<Exp> getExpListByUserId(String userId);
	public int getTotalGetMileage(String userId);
	public int getTotalUseMileage(String userId);
	public int getTotalGetExp(String userId);
	public int getTotalUseExp(String userId);
	public List<Mileage> getMileageList(PointCondition condition);
	public int getMileageListCnt(PointCondition condition);
	public List<Exp> getExpList(PointCondition condition);
	public int getExpListCnt(PointCondition condition);
	public List<PointType> getPointTypeList(PointCondition condition);
	public int getPointTypeListCnt(PointCondition condition);
	public List<MileExp> getMileExpListByMile(PointCondition condition);
	public List<MileExp> getMileExpListByExp(PointCondition condition);
	public int getMileExpListByMileCnt(PointCondition condition);
	public int getMileExpListByExpCnt(PointCondition condition);
	public int getMileTotal(String userId);
	public int getExpTotal(String userId);
	public int getStatusStage1(GameData data);
	public int getStatusStage2(GameData data);
	public int getStatusStage3(GameData data);
	public int getStatusStage4(GameData data);
	public int getStatusStage5(GameData data);
	public int getStatusStage6(GameData data);
	public int getStatusStage7(GameData data);
	public int getStatusStage8(GameData data);
	public int getStatusStage9(GameData data);
	public int getStatusStage10(GameData data);
	public int getStatusStage11(GameData data);
	public int getStatusStage12(GameData data);
	public int getStatusStage13(GameData data);
	public int getStatusStage14(GameData data);
	public int getStatusStage15(GameData data);
	
}
