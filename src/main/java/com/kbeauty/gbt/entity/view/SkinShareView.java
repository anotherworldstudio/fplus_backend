package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class SkinShareView  extends CommonView{
	
	private String skinid;
	private String userId;
	private String userName;
	private String aiUrl;
	
	private String title;
	private String comment;	
	private int upperPer;
	private int totalScore;
	private int skinAge;
	private int realAge;
	
	private int wrinkle;
	private int pigment;
	private int trouble;
	private int pore;
	private int redness;
	
	private String wrinkleComment;
	private String pigmentComment;
	private String troubleComment;
	private String poreComment;
	private String rednessComment;
	
	public String getLowerPer() {
		return "또래 사용자 " + (100 - upperPer) + "%보다 피부가 좋아요";
	}
}
