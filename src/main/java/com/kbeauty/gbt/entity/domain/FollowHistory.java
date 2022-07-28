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
@Table(name = "FOLLOWH")
@Data
@EqualsAndHashCode(callSuper=false)
public class FollowHistory  extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "followid")	private String followId;
	
	@Column(name = "actionuserid")	private String actionUserId;
	
	@Column(name = "actiontype")	private String actionType;
	
	@Column(name = "actionresult")	private String actionResult;
	
	@Column(name = "skipcode")	private String skipCode;
	
}
