package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.Checked;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "WEATHERPRODUCT")
@Data
@EqualsAndHashCode(callSuper=false)
public class WeatherProduct  extends CommonDomain {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	
	@Column(name = "weatherid")	private String weatherId       ;
	
	@Column(name = "refId")	private String refId       ;
	@Column(name = "priority")	private String priority       ;
	
}
