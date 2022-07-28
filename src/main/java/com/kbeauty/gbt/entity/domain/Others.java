package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.Checked;
import com.kbeauty.gbt.entity.view.ImageData;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "OTHERS")
@Data
@EqualsAndHashCode(callSuper=false)
public class Others  extends CommonDomain implements Comparable<Others> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;
	
	@Column(name = "contentid")	private String contentId       ;
	
	@Column(name = "otherid")	private String otherId       ;
	@Column(name = "othertype")	private String otherType       ;
	@Column(name = "othercode")	private String otherCode       ;
	@Column(name = "othername")	private String otherName       ;
	@Column(name = "othervalue")	private String otherValue       ;	
	@Column(name = "otherorder")	private int otherOrder       ;
	@Column(name = "otherstatus")	private String otherStatus       ;
	
	@Transient private String imgUrl       ;	
	@Transient private FileInfo fileInfo;
	
	// 제품 전용 필드 
	@Transient private String title        ;
	@Transient private String curr        ;
	@Transient private double price       ;
	@Transient private String priceUrl;
	
	
	public boolean getChecked() {
		return Checked.YES.getVal().equals(otherValue);
	}
	 
	@Override 
	public int compareTo(Others others) { 
		return this.otherOrder -  others.getOtherOrder();
	}
}
