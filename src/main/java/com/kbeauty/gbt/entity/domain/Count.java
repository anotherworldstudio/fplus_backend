package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "COUNT")
@Data
public class Count {
	@Id
	@Column(name = "contentid")	private String contentId;
	@Column(name = "viewcnt")	private int viewCnt  ;
	@Column(name = "likecnt")	private int likeCnt  ;
	@Column(name = "favcnt")	private int favCnt   ;	
	@Column(name = "replycnt")    private int replyCnt   ;

}
