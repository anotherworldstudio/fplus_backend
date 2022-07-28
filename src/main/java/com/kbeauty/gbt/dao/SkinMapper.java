package com.kbeauty.gbt.dao;

import java.util.List;
import java.util.Map;

import com.kbeauty.gbt.entity.domain.Code;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinDoctor;
import com.kbeauty.gbt.entity.domain.SkinGroup;
import com.kbeauty.gbt.entity.domain.SkinRanking;
import com.kbeauty.gbt.entity.domain.SkinTypeTest;
import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;
import com.kbeauty.gbt.entity.view.AiRecommandRaw;
import com.kbeauty.gbt.entity.view.DiaryView;
import com.kbeauty.gbt.entity.view.SkinRank;
import com.kbeauty.gbt.entity.view.SkinRankingView;
import com.kbeauty.gbt.entity.view.SkinTypeView;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.UserSkinListView;
import com.kbeauty.gbt.entity.view.UserSkinView;

public interface SkinMapper {	
	public List<AiRecommandRaw> getAiRecommandList();
	public List<Code>getCodeData(Code code);
	
	public List<UserSkinListView> getUserSkinList(UserSkinCondition condition);	
	public int getUserSkinListCnt(UserSkinCondition condition);
	public int getREQSkinListCnt(UserSkinCondition condition);
	public List<UserSkinListView> getREQSkinList(UserSkinCondition condition);
	
	public List<DiaryView> getMonthSkinData(UserSkinCondition condition);	
	public DiaryView getDailySkinData(UserSkinCondition condition);	
	public List<Skin> getTermDailySkinData(UserSkinCondition condition);
	
	public List<SkinGroup> getSkinGroupList(UserSkinCondition condition);	
	public int getSkinGroupListCnt(UserSkinCondition condition);
	
	public SkinRank getSkinRank(SkinRank skinRank); 
	public SkinRank getSkinAge01(SkinRank skinRank); 
	public SkinRank getSkinAge02(SkinRank skinRank); 
	
	public int getSkinAge(int score); 
	public int updateStatusBySkinId(String skinId);
	public SkinDoctor getMySkinDoctorScore(SkinDoctor skin_user);
	
	public SkinDoctor getAVGScore(String skinId);
	public List<SkinRanking> getSkinRankingTop10(SkinRankingView view);
	public List<Skin> getFiveDaySkin(String userId);
	public List<SkinTypeTest> getFinishTestList(String userId);
	public List<SkinTypeTest> getProgressTestList(String userId);
	public int getSkinTestMaxStep(String testId);
	public SkinTypeView getSkinTypeTestScore(SkinTypeTest test);
	public List<SkinTypeTestStep> findByTestId(String testId);
	public SkinTypeTestStep findByTestIdAndStep(SkinTypeTestStep step);
}
