package com.kbeauty.gbt.dao;

import java.util.List;
import java.util.Map;

import com.kbeauty.gbt.entity.domain.Code;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.LessonFace;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinDoctor;
import com.kbeauty.gbt.entity.domain.SkinGroup;
import com.kbeauty.gbt.entity.domain.SkinRanking;
import com.kbeauty.gbt.entity.domain.SkinTypeTest;
import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.view.AiRecommandRaw;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.DiaryView;
import com.kbeauty.gbt.entity.view.LessonCondition;
import com.kbeauty.gbt.entity.view.SkinRank;
import com.kbeauty.gbt.entity.view.SkinRankingView;
import com.kbeauty.gbt.entity.view.SkinTypeView;
import com.kbeauty.gbt.entity.view.TrainingCondition;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.UserCondition;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.UserSkinListView;
import com.kbeauty.gbt.entity.view.UserSkinView;

public interface LessonMapper {	
	
	List<Lesson> getLessonList(LessonCondition condition);	
	int getLessonListCnt(LessonCondition condition);
	
			
	List<Lesson> getLessonListByUserFace(LessonCondition condition);	
	int getLessonListCntByUserFace(LessonCondition condition);
	
	List<UserFace> getUserFaceList(String userId);
	long getLessonSeq();
	
	List<LessonFace> getLessonFaceListByLessonIdAndType(LessonFace face);
	List<LessonFace> getLessonFaceListByLessonId(LessonFace face);
	
	List<LessonFace> getLessonFaceOverlapCheck(LessonFace face);
	
	Training getTrainingByLesson(Training training);
	int getOldTrainingCheck(Training training);
	List<TrainingView> getTrainingListByUser(User user);
	
	
	List<Training> getTrainingList(TrainingCondition condition);
	int getTrainingListCnt(TrainingCondition condition);
}
