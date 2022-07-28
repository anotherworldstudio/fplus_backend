package com.kbeauty.gbt.entity.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.SkinRanking;

import lombok.Data;

@Data
public class SkinRankingView extends CommonView{

	private String day;
	private int age;
	private String sex;
	private List<SkinRanking> rankingList;
	
	public void setDateToday() {
		LocalDate now = LocalDate.now();
		day = now.format(DateTimeFormatter.BASIC_ISO_DATE);
	}
	
}