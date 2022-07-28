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
@Table(name = "EVALUATE")
@Data
@EqualsAndHashCode(callSuper=false)
public class Evaluate extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;              
	@Column(name = "evaluateid")		private String evaluateId;
	@Column(name = "lessonid")			private String lessonId;
	@Column(name = "trainingid")		private String trainingId;
	@Column(name = "userid")			private String userId;
	@Column(name = "comment")			private String comment;
	@Column(name = "grade")				private String grade;
	@Column(name = "score")				private int score;
	@Column(name = "editskintone")		private String editSkinTone;
	@Column(name = "editseasonColor")	private String editSeasonColor;
	@Column(name = "editfacialcontour")	private String editFacialContour;
	
	

//	@Transient private String lessonTitle     ;
//	@Transient private String userName     ;
//	@Transient private String ageName     ;
//	@Transient private String genderName     ;
//	@Transient private String seasonColorName     ;
//	@Transient private String facialContourName     ;
//	@Transient private String skinToneName     ;
	@Transient private String gradeName     ;
	@Transient private String editFacialContourName     ;
	@Transient private String editSeasonColorName     ;
	@Transient private String editSkinToneName     ;
	
	public void setEnumName() {
		gradeName = CommonUtil.getValue(Grade.values(), grade);
		editSeasonColorName = CommonUtil.getValue(SeasonColor.values(), editSeasonColor);
		editFacialContourName = CommonUtil.getValue(FacialContour.values(), editFacialContour);
		editSkinToneName = CommonUtil.getValue(SkinTone.values(), editSkinTone);
		
	}

	public Evaluate(Training training,String userId) {
		this.evaluateId = CommonUtil.getGuid(); 
		this.lessonId = training.getTrainingId();
		this.trainingId = training.getLessonId();
		this.userId = userId;
		this.comment = training.getComment();
		this.grade = training.getGrade();
		this.score = training.getScore();
		this.editSkinTone = training.getEditSkinTone();
		this.editSeasonColor = training.getEditSeasonColor();
		this.editFacialContour = training.getEditFacialContour();
	}
	

}
