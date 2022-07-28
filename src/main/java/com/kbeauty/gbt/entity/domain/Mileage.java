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
@Table(name = "MILEAGE")
@Data
@EqualsAndHashCode(callSuper = false)
public class Mileage extends CommonDomain {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq               ;	
	@Column(name = "mileid")	    private String mileId;
	@Column(name = "userid")	    private String userId;
	@Column(name = "bigtype")	    private String bigType;
	@Column(name = "miletype")	    private String mileType;
	@Column(name = "plusminus") 	private String plusMinus;
	@Column(name = "mile") 		private int mile;
	@Column(name = "oldtotal") 	private int oldTotal;
	@Column(name = "newtotal") 	private int newTotal;
	
	@Transient
	private String bigTypeName;
	@Transient
	private String mileTypeName;
	@Transient
	private String userName;
	
	
	public void setTypeName() {
		mileTypeName = CommonUtil.getValue(PointUseGetTypeDetail.values(), mileType);
		bigTypeName = CommonUtil.getValue(PointUseGetType.values(), bigType);
	}
	
	public boolean isEmpty() {
		return  
				StringUtil.isEmpty(userId) ||
				StringUtil.isEmpty(mileType);
	}

	
}
