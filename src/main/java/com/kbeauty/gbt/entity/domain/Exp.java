package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.PointUseGetType;
import com.kbeauty.gbt.entity.enums.PointUseGetTypeDetail;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "EXP")
@Data
@EqualsAndHashCode(callSuper = false)
public class Exp extends CommonDomain {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "expid")	    private String expId;
	@Column(name = "userid")	    private String userId;
	@Column(name = "bigtype")	    private String bigType;
	@Column(name = "exptype")	    private String expType;
	@Column(name = "plusminus") 	private String plusMinus;
	@Column(name = "exp") 		private int exp;
	@Column(name = "oldtotal") 	private int oldTotal;
	@Column(name = "newtotal") 	private int newTotal;
	
	@Transient
	private String bigTypeName;
	@Transient
	private String expTypeName;
	@Transient
	private String userName;
	
	
	public void setTypeName() {
		expTypeName = CommonUtil.getValue(PointUseGetTypeDetail.values(), expType);
		bigTypeName = CommonUtil.getValue(PointUseGetType.values(), bigType);
	}
	
	public boolean isEmpty() {
		return  
				StringUtil.isEmpty(userId) ||
				StringUtil.isEmpty(expType);
	}
	
	
}
