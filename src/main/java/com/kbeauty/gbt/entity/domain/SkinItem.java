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
@Table(name = "SKINITEM")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinItem extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "groupid")	    private String groupId;
	@Column(name = "itemid") 	private String itemId;
	
	@Column(name = "refgroup") 	private String refGroup;
	@Column(name = "refid") 	private String refId;
}
