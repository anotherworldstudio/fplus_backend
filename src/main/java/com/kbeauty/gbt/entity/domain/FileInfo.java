package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.WeatherType;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "FILEINFO")
@Data
@EqualsAndHashCode(callSuper = false)
public class FileInfo extends CommonDomain {
	
	@Id
	private long seq;
	
	@Column(name = "filename") private String filename;
	@Column(name = "type") private String type;
	@Column(name = "width") private int width;
	@Column(name = "height") private int height;
	@Column(name = "extension") private String extension;
	
	@Transient private float ratio;
}
