package com.kbeauty.gbt.dao;

import com.kbeauty.gbt.entity.domain.*;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ClassView;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.RecruitCondition;

import java.util.List;
import java.util.Map;

public interface RecruitMapper {
	Long getRecruitSeq();
	
	List<Recruit> getRecruitList(RecruitCondition condition);

	int getRecruitListCnt(RecruitCondition condition);

	public List<Resources> resourcesFile();


}
