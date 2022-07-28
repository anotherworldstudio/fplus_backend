package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.AgeGroup;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.enums.FacialContour;
import com.kbeauty.gbt.entity.enums.Gender;
import com.kbeauty.gbt.entity.enums.Grade;
import com.kbeauty.gbt.entity.enums.LessonCategory1;
import com.kbeauty.gbt.entity.enums.LessonCategory2;
import com.kbeauty.gbt.entity.enums.SeasonColor;
import com.kbeauty.gbt.entity.enums.SkinTone;
import com.kbeauty.gbt.entity.enums.TrainingStatus;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity 
@Table(name = "TRAINING")
@Data
@EqualsAndHashCode(callSuper=false)
public class Training extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;              
	@Column(name = "trainingid")		private String trainingId;
	@Column(name = "lessonid")			private String lessonId;
	@Column(name = "userid")			private String userId;
	@Column(name = "beforeimg")			private String beforeImg;
	@Column(name = "afterimg")			private String afterImg;
	@Column(name = "comment")			private String comment; 	
	@Column(name = "status")			private String status;
	@Column(name = "age" ) 				private String age;
	@Column(name = "gender" ) 			private String gender;
	@Column(name = "grade")				private String grade;
	@Column(name = "score")				private int score;
	@Column(name = "skintone")			private String skinTone;
	@Column(name = "seasoncolor")		private String seasonColor;
	@Column(name = "seasoncoloruser1")	private String seasonColorUser1;
	@Column(name = "seasoncoloruser2")	private String seasonColorUser2;
	@Column(name = "seasoncoloruser3")	private String seasonColorUser3;
	@Column(name = "seasoncolorai1")	private String seasonColorAi1;
	@Column(name = "seasoncolorai2")	private String seasonColorAi2;
	@Column(name = "seasoncolorai3")	private String seasonColorAi3;
	@Column(name = "facialcontour")		private String facialContour;
	@Column(name = "editskintone")		private String editSkinTone;
	@Column(name = "editseasoncolor")	private String editSeasonColor;
	@Column(name = "editfacialcontour")	private String editFacialContour;
	@Column(name = "category1")			private String category1;
	@Column(name = "category2")			private String category2;
	@Column(name = "finishdate")		private String finishDate;
	@Column(name = "finishtime")		private String finishTime;
	@Column(name = "evaluatedate")		private String evaluateDate;
	@Column(name = "evaluatetime")		private String evaluateTime;
	@Column(name = "evaluateuserid")	private String evaluateUserId;
	

	@Transient private String lessonTitle     ;
	@Transient private String userName     ;
	@Transient private String statusName     ;
	@Transient private String categoryName1     ;
	@Transient private String categoryName2     ;
	@Transient private String ageName     ;
	@Transient private String genderName     ;
	@Transient private String seasonColorName     ;
	@Transient private String facialContourName     ;
	@Transient private String skinToneName     ;
	@Transient private String gradeName     ;
	@Transient private String editFacialContourName     ;
	@Transient private String editSeasonColorName     ;
	@Transient private String editSkinToneName     ;
	
	public void setEnumName() {
		gradeName = CommonUtil.getValue(Grade.values(), grade);
		statusName = CommonUtil.getValue(TrainingStatus.values(), status);
		categoryName1 = CommonUtil.getValue(LessonCategory1.values(), category1);
		categoryName2 = CommonUtil.getValue(LessonCategory2.values(), category2);
		ageName = CommonUtil.getValue(AgeGroup.values(), age);
		genderName = CommonUtil.getValue(Gender.values(), gender);
		seasonColorName = CommonUtil.getValue(SeasonColor.values(), seasonColor);
		facialContourName = CommonUtil.getValue(FacialContour.values(), facialContour);
		skinToneName = CommonUtil.getValue(SkinTone.values(), skinTone);
		editSeasonColorName = CommonUtil.getValue(SeasonColor.values(), editSeasonColor);
		editFacialContourName = CommonUtil.getValue(FacialContour.values(), editFacialContour);
		editSkinToneName = CommonUtil.getValue(SkinTone.values(), editSkinTone);
	}
	
	public void setGradeTime(String userId) {
		String dateStr = CommonUtil.getSysTime();
		
		evaluateDate = dateStr.substring(0, 8);
		evaluateTime = dateStr.substring(8);
		evaluateUserId = userId;
	}
	
	public void setFinishTime() {
		String dateStr = CommonUtil.getSysTime();
		
		finishDate = dateStr.substring(0, 8);
		finishTime = dateStr.substring(8);
	}
	
	public boolean isEmpty() {
		return skinTone == null || "".equals(skinTone) || 
				seasonColor == null || "".equals(seasonColor) || 
				facialContour == null || "".equals(facialContour) ||
				age == null || "".equals(age) ||
				gender == null || "".equals(gender) ;
		
	}

}
