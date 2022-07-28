package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class SkinRank  extends CommonView{
	/**
	 * 조회 항목
	 */
	private String startDate;
	private String endDate;
	private int inputScore;
	
	/**
	 * 출력 항목 
	 */	
	private String skinid;
	private int score;
	private int ranking;
	private int cnt;
}
