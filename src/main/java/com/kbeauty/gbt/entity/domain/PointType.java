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
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "POINTTYPE")
@Data
@EqualsAndHashCode(callSuper = false)
public class PointType extends CommonDomain {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;	
	@Column(name = "pointtypeid")	private String pointTypeId;
	@Column(name = "bigtype")	    private String bigType;
	@Column(name = "systemType") 	private String systemType;
	@Column(name = "typeName") 		private String typeName;
	@Column(name = "plusminus") 	private String plusMinus;
	@Column(name = "mileage")	 	private int mileage;
	@Column(name = "exp") 			private int exp;
	
	@Transient
	private String bigTypeName;
	@Transient
	private String systemTypeName;
	
	
	public void setTypeName() {
		bigTypeName = CommonUtil.getValue(PointUseGetType.values(), bigType);
		systemTypeName = CommonUtil.getValue(PointUseGetTypeDetail.values(), systemType);
	}
	
	public boolean isEmpty() {
		return  
				StringUtil.isEmpty(pointTypeId) ||
				StringUtil.isEmpty(bigType) ||
				StringUtil.isEmpty(systemType)||
				StringUtil.isEmpty(typeName) ||
				StringUtil.isEmpty(plusMinus);
			
	}
}
