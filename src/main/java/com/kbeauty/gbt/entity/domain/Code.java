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
@Table(name = "NCODE")
@Data
@EqualsAndHashCode(callSuper = false)
public class Code extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	public static final String SKIN_BASE_SCORE = "10000";
	public static final String SKIN_AGE_WEIGHT = "10010";
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;	
	
	@Column(name = "mcode")	    private String mCode;
	@Column(name = "ncode")	    private String nCode;
	@Column(name = "codename")	    private String codeName;
	@Column(name = "codeval")	    private String codeVal;
	@Column(name = "codetype")	    private String codeType;
	@Column(name = "startdate")	    private String startDate;
	@Column(name = "enddate")	    private String endDate;
	@Column(name = "status")	    private String status;
	
	// TODO 수정 필요
	public int getIntVal() {
		return Integer.parseInt(codeVal);
	}
	
}
