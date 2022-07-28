package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "PRICE")
@Data
@EqualsAndHashCode(callSuper = false)
public class Price  extends CommonDomain {
	
	@Column(name = "contentid")	private String contentId   ;
	@Id
	@Column(name = "priceid")	private String priceId     ;
	@Column(name = "pricetype")	private String priceType   ;
	private String curr        ;
	private double price       ;
	private double qty         ;
	@Column(name = "qtyunit")	private String qtyUnit     ;
	@Column(name = "discountamt")	private double discountAmt ;
	@Column(name = "discountrate")	private double discountRate;
	@Column(name = "priceurl")	private String priceUrl;

}
