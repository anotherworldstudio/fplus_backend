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
@Table(name = "SKINLEVEL")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinLevel extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq            ;	
	@Column(name = "groupid")	private String groupId;
	@Column(name = "itemid") 	private String itemId;
	@Column(name = "itemcode") 	private String itemCode;
	@Column(name = "fromval") 	private String fromVal;
	@Column(name = "toval") 	private String toVal;
	private String status;
	@Column(name = "leveltype") 	private String levelType;
	@Column(name = "pickcount") 	private int pickCount;
}
