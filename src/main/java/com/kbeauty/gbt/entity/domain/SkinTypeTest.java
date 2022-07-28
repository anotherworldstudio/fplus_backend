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
@Table(name = "SKINTYPETEST")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinTypeTest extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "testid")	    private String testId;	
	@Column(name = "userid") 		private String userId;
	@Column(name = "status")	    private String status;
	@Column(name = "score")			private float score;
	
	
	
}
