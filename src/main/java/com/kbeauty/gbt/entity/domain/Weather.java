package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.PointUseGetType;
import com.kbeauty.gbt.entity.enums.WeatherType;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.util.CommonUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "WEATHER")
@Data
@EqualsAndHashCode(callSuper = false)
public class Weather extends CommonDomain {
	
	@Id
	private long seq;
	
	@Column(name = "weatherid") private String weatherId;
	@Column(name = "weathertype") private String weatherType;
	@Column(name = "weathercode") private String weatherCode;
	@Column(name = "weathername") private String weatherName;
	@Column(name = "weathermsg") private String weatherMsg;
	@Column(name  = "status") private String status;
	
	
	@Transient
	private String refId;
	@Transient
	private ContentView contentView;
	@Transient
	private String productSeq;
	@Transient
	private String typeName;
	
	
	public void setTypeName() {
		typeName = CommonUtil.getValue(WeatherType.values(),weatherType);
	}
	
	
}
