package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "USERFACE")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserFace extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "userfaceid")	private String userFaceId;	
	@Column(name = "userid")	private String userId;
	@Column(name = "thumbnail")	private String thumbnail;
	@Column(name = "facename")	private String faceName;
	@Column(name = "age")	private String age;
	@Column(name = "gender")	private String gender;
	@Column(name = "skintone")	private String skinTone;
	@Column(name = "seasoncolor")	private String seasonColor;
	@Column(name = "seasoncoloruser1")		private String seasonColorUser1;
	@Column(name = "seasoncoloruser2")		private String seasonColorUser2;
	@Column(name = "seasoncoloruser3")		private String seasonColorUser3;
	@Column(name = "seasoncolorai1")		private String seasonColorAi1;
	@Column(name = "seasoncolorai2")		private String seasonColorAi2;
	@Column(name = "seasoncolorai3")		private String seasonColorAi3;
	@Column(name = "facialcontour")	private String facialContour;
	@Column(name = "status")	private String status;
	@Column(name = "who")	private String who;
	
	

}
