package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.enums.ResourceCategory;
import com.kbeauty.gbt.entity.enums.ResourceType;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.util.CommonUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "RESOURCES")
@Data
@EqualsAndHashCode(callSuper = false)
public class Resources extends CommonDomain {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;
	
	@Column(name = "contentid")	    private String contentId;
	@Column(name = "resourceid") 	private String resourceId;	
	@Column(name = "resourcetype") 	private String resourceType;
	@Column(name = "resourcename") 	private String resourceName;
	@Column(name = "resourcetitle") 	private String resourceTitle;	
	@Column(name = "resourcecontent") 	private String resourceContent;
	@Column(name = "resourcecategory") 	private String resourceCategory;
	private String url; // 입력 전용 ==> 동영상/이미지 link 및 youtube url 
	private String dir;
	private String filename;
	private int orders;
	
	@Transient private FileInfo fileInfo;
	@Transient private String link; //조회 전용 
	@Transient private String resourceUrl; //조회 전용 
	
	@Transient private String categoryName;
	
	
	public void setEnumName() {
		if(resourceCategory==null || "".equals(resourceCategory)) {
			categoryName = "해당없음";
		}else {
			categoryName = CommonUtil.getValue(ResourceCategory.values(), resourceCategory);
		}
	}	
	public boolean isStorage() {
		return ResourceType.isStorageUrl(resourceType);
	}

}
