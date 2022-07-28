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
@Table(name = "USERSKIN")
@Data
@EqualsAndHashCode(callSuper=false)
public class UserSkin extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long seq;

	@Column(name = "userid")	private String userId;
	
	private String trouble01  ;
	private String trouble02  ;
	private String trouble03  ;
	private String trouble04  ;
	private String trouble05  ;
	private String skintype01 ;
	private String skintype02 ;
	private String skintype03 ;
	private String skintype04 ;
	private String skintype05 ;
	private String skintype06 ;
	private String skintype07 ;
	private String skintype08 ;
	

}
