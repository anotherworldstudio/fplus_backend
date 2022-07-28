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
@Table(name = "SKINRANKING")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinRanking extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;	
	@Column(name = "userid")	    private String userId;	
	@Column(name = "skinid") 	private String skinId;
	@Column(name = "score" ) 	private double score;
	@Column(name = "status") 	private String status;
	@Column(name = "sex") 	private String sex;
	@Column(name = "age") 	private int age;
	
	@Transient private String userName;   
	
	
	

}
