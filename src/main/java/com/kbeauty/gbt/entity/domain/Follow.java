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
@Table(name = "FOLLOW")
@Data
@EqualsAndHashCode(callSuper=false)
public class Follow  extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "followid")	private String followId;
	@Column(name = "followtype")	private String followType;
	
	@Column(name = "userid")	private String userId;
	@Column(name = "followerid")	private String followerId;
	
	@Column(name = "reqtype")	private String reqType;
	@Column(name = "reqblock")	private String reqBlock;
	@Column(name = "restype")	private String resType;
	@Column(name = "resblock")	private String resBlock;
	
	@Column(name = "followyn") private String followYn;
	
	
	public void removeReqInfo() {
		reqType = null;
		reqBlock = null;
		followYn = null;
	}
	
	public void removeResInfo() {
		resType = null;
		resBlock = null;		
		followYn = null;
	}

}
