package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.enums.LessonCategory1;
import com.kbeauty.gbt.entity.enums.LessonCategory2;
import com.kbeauty.gbt.entity.enums.LessonType;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity 
@Table(name = "LESSON")
@Data
@EqualsAndHashCode(callSuper=false)
public class Lesson extends CommonDomain {
	
	@Id	
	private long seq                ;
	@Column(name = "lessonid")		private String lessonId       ;
	@Column(name = "ownerid")		private String ownerId       ;
	@Column(name = "title")			private String title       ;
	@Column(name = "content")		private String content;
	@Column(name = "viewtype")		private String viewType;
	@Column(name = "startdate")		private String startDate;
	@Column(name = "enddate")		private String endDate;
	@Column(name = "mainurl")		private String mainUrl;
	@Column(name = "mainlink")		private String mainLink;
	@Column(name = "maindir")		private String mainDir;
	@Column(name = "mainfilename")	private String mainFileName;
	@Column(name = "category1")		private String category1;
	@Column(name = "category2")		private String category2;
	@Column(name = "status")		private String status;
	@Column(name = "active")		private String active;
	@Column(name = "orders")		private int orders;
	
	//Enum으로 설정
		@Transient private String viewTypeName     ;
		@Transient private String activeName     ;
		@Transient private String statusName     ;
		@Transient private String categoryName1     ;
		@Transient private String categoryName2     ;
		
		public void setEnumName() {
			activeName = CommonUtil.getValue(Active.values(), active);
			viewTypeName = CommonUtil.getValue(ContentViewType.values(), viewType);
			statusName = CommonUtil.getValue(ContentStatus.values(), status);
			categoryName1 = CommonUtil.getValue(LessonCategory1.values(), category1);
			categoryName2 = CommonUtil.getValue(LessonCategory2.values(), category2);
			
		}

		public boolean isMainImg() {
			return ! StringUtil.isEmpty(mainDir);
		}
}
