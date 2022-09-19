package com.kbeauty.gbt.dao;

import com.kbeauty.gbt.entity.domain.*;
import com.kbeauty.gbt.entity.view.*;

import java.util.List;
import java.util.Map;

public interface RecruitMapper {
	Long getRecruitSeq();
	
	List<Recruit> getRecruitList(RecruitCondition condition);

	List<Premium> getPremiumByList(RecruitByCondition recruitByCondition);
	int getRecruitListCnt(RecruitCondition condition);

	int getPremiumByListCnt(RecruitByCondition recruitByCondition);

	public List<Resources> resourcesFile();


}
