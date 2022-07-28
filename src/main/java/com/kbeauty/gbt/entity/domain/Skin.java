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
@Table(name = "SKIN")
@Data
@EqualsAndHashCode(callSuper = false)
public class Skin extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "skinid")	    private String skinId;
	@Column(name = "userid")	    private String userId;
	@Transient private String userName;
	@Column(name = "groupid")	    private String groupId;
	@Transient 	    private String groupName;
	
	@Column(name = "aiurl") 	private String aiUrl;
	private String dir;
	@Column(name = "orgimg") 	private String orgImg;
	@Column(name = "aiimg") 	private String aiImg;
	@Column(name = "dday") 	private String dDay;	
	private String comment;
	private String status;
	@Transient private String statusName;
	
	@Transient private int totalScore;
	@Transient private double decimalTotal;
}
