package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SKINRESOURCE")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinResource extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "skinid")	    private String skinId;	
	@Column(name = "itemcode") 		private String itemCode;
	@Column(name = "contentid")	    private String contentId;
	@Column(name = "resourceid") 	private String resourceId;
	@Column(name = "contenttype")	private String contentType;
	
}
