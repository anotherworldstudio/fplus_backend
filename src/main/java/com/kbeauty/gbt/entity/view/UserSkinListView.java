package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class UserSkinListView {
	private long seq;
	private String userId;
	private String userName;
	private String skinid;
	private String groupid;
	private String aiurl;
	private String dDay;	
	private String ownertype;
	private String status; 
	
	private int totalScore;
	private double decimalTotal;
		
	private int wrinkleScore;
	private double wrinkleAiScore;
	
	private int rednessScore;
	private double rednessAiScore;
	
	private int poreScore;
	private double poreAiScore;
	
	private int troubleScore;
	private double troubleAiScore;
	
	private int pigmentScore;
	private double pigmentAiScore;
	
}
