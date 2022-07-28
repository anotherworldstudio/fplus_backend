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
@Table(name = "LIKES")
@Data
@EqualsAndHashCode(callSuper = false)
public class Likes extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	@Column(name = "contentid")
	private String contentId;
	@Column(name = "resourceid")
	private String resourceId;
	@Column(name = "liketype")
	private String likeType;
	@Column(name = "likeid")
	private String likeId;
	@Column(name = "ownerid")
	private String ownerId;
	
	@Transient private int likeCnt;

}
