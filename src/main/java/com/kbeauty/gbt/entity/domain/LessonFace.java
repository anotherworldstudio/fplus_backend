package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.kbeauty.gbt.entity.enums.LessonCategory1;
import com.kbeauty.gbt.entity.enums.LessonCategory2;
import com.kbeauty.gbt.entity.enums.LessonFaceType;
import com.kbeauty.gbt.entity.enums.LessonType;
import com.kbeauty.gbt.entity.enums.SeasonColor;
import com.kbeauty.gbt.entity.enums.SkinTone;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity 
@Table(name = "LESSONFACE")
@Data
@EqualsAndHashCode(callSuper=false)
public class LessonFace extends CommonDomain {
	
	@Id	
	private long seq                ;
	@Column(name = "lessonfaceid")	private String lessonFaceId   ;
	@Column(name = "lessonid")	private String lessonId       ;
	@Column(name = "type")	private String type;
	@Column(name = "category")	private String category      ;
	
	@Transient private String typeName     ;
	@Transient private String categoryName    ;
	
	public void setEnumName() {
		typeName = CommonUtil.getValue(LessonFaceType.values(), type);
		if(LessonFaceType.AGE.getCode().equals(type)){
			categoryName = CommonUtil.getValue(AgeGroup.values(), category);
		}else if(LessonFaceType.FACIALCONTOUR.getCode().equals(type)){
			categoryName = CommonUtil.getValue(FacialContour.values(), category);
		}else if(LessonFaceType.SEASONCOLOR.getCode().equals(type)){
			categoryName = CommonUtil.getValue(SeasonColor.values(), category);
		}else if(LessonFaceType.SKINTONE.getCode().equals(type)){
			categoryName = CommonUtil.getValue(SkinTone.values(), category);
		}else if(LessonFaceType.GENDER.getCode().equals(type)){
			categoryName = CommonUtil.getValue(Gender.values(), category);
		}else if(LessonFaceType.TYPE.getCode().equals(type)){
			categoryName = CommonUtil.getValue(LessonType.values(), category);
		}
	}
}
