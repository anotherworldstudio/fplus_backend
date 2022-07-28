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
@Table(name = "SKINSCORE")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinScore extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "skinid")	    private String skinId;	
	@Column(name = "itemcode") 	private String itemCode;
	@Transient 	private String itemName;
	
	private double score;
	private String comment;
	
	@Column(name = "ownerid") 	    private String ownerId;
	@Column(name = "ownertype") 	private String ownerType;
	
	@Column(name = "adjustscore") 	private int adjustScore;
	

}
