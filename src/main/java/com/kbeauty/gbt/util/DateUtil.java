package com.kbeauty.gbt.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public class DateUtil {
	
	public static String getLastDay(String currDate) {
		LocalDate parse = LocalDate.parse(currDate, DateTimeFormatter.BASIC_ISO_DATE)
								   .with(TemporalAdjusters.lastDayOfMonth());
		String result = parse.format(DateTimeFormatter.BASIC_ISO_DATE);
		return result;
	}
	
	public static String[] getBetweenDate(String currDate) {
		
		String startDate = currDate.substring(0, 6) + "01";
		String endDate =  getLastDay(currDate);
		
		String[] result = new String[2];
		result[0] = startDate;
		result[1] = endDate;
		return result;
	}
	
	public static String[] getBetweenAgeDate(String birthDay) {		
		LocalDate now = LocalDate.now();
		String currDate = now.format(DateTimeFormatter.BASIC_ISO_DATE);
		return getBetweenAgeDate(birthDay, currDate);
	}
	
	public static String[] getBetweenAgeDate(String birthDay, String currDate) {
		// 만나이 생성 
		LocalDate now = LocalDate.parse(currDate, DateTimeFormatter.BASIC_ISO_DATE);
		LocalDate parsedBirthDate = LocalDate.parse(birthDay, DateTimeFormatter.BASIC_ISO_DATE);
	 
		int manAge = now.minusYears(parsedBirthDate.getYear()).getYear();

		if (parsedBirthDate.plusYears(manAge).isAfter(now)) { 
			manAge = manAge -1;
		}
		
		// 만나이에 해당되는 연령된 생성 
		int startAge = (manAge / 10) * 10; // 20 
		int endAge = startAge + 10; // 30 
		
		// 연령에 해당하는 연도 생성 
		LocalDate startDate = now.minusYears(endAge);
		LocalDate endDate = now.minusYears(startAge);
		startDate = startDate.plusDays(1); // 30대는 포함이 아니기 때문에 마지막 날에서 하루를 뺀다 
		
		String[] result = new String[2];
		
		result[0] = startDate.format(DateTimeFormatter.BASIC_ISO_DATE);
		result[1] = endDate.format(DateTimeFormatter.BASIC_ISO_DATE);
		
		return result;
		
	}
	
	public static int getManAge(String birthDay) {
		LocalDate now = LocalDate.now();
		String currDate = now.format(DateTimeFormatter.BASIC_ISO_DATE);
		return getManAge(birthDay, currDate);
	}
	
	public static int getManAge(String birthDay, String currDate) {
		
		LocalDate now = LocalDate.parse(currDate, DateTimeFormatter.BASIC_ISO_DATE);
		LocalDate parsedBirthDate = LocalDate.parse(birthDay, DateTimeFormatter.BASIC_ISO_DATE);
	 
		int manAge = now.minusYears(parsedBirthDate.getYear()).getYear();

		if (parsedBirthDate.plusYears(manAge).isAfter(now)) { 
			manAge = manAge -1;
		}
	 
		return manAge;    	
	}
	
	
	public static void main(String[] args) {
//		System.out.println(
//		DateUtil.getLastDay("20210205")
//		);
		
		DateUtil.getBetweenAgeDate("20000210", "20210211");
	}

}
