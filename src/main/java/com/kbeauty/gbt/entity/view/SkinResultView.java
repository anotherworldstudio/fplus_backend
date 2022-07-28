package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.ai.FaceEnum;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;

import lombok.Data;

@Data
public class SkinResultView  extends CommonView{
	
	private String groupId;
	private String userId;
	private String imgId;
	private String imgUrl;		
	private int totalScore;
	private double decimalTotal;
	private String grade;
	private String skinComment;
	private String processDate;
	private List<SkinResult> skinResultList;
	private List<SkinInfo> skinInfoList;
	private ImageData imgData;
	
	private int skinRank;
	private int skinAge01;
	private int skinAge02;
	private int skinRankPercent;
	
	private String shareLink;  
	private String userName; //홈페이지 용 등록자명
	
	private String TTUrl;
	private String TBUrl;
	private String ULUrl;
	private String URUrl;
	private String FLUrl;
	private String FMUrl;
	private String FRUrl;
	private String GLUrl;
	private String ELUrl;
	private String ERUrl;
	private String CLUrl;
	private String CRUrl;
	private String MOUrl;
	private String MLUrl;
	private String MRUrl;
			              
	
	public void generateData() {
		int tempScore = 0;
		for (SkinResult skinResult : skinResultList) {
			tempScore += skinResult.getScore();
		}
		decimalTotal =(double)tempScore/skinResultList.size();
		decimalTotal = (double)(Math.round(decimalTotal*10.0)/10.0);
		totalScore = (int)Math.round(tempScore / skinResultList.size());
		
		if(totalScore >= 90) {
			grade = "Best";
		}else if(totalScore >= 80) {			
			grade = "Good"; //PERFECT
		}else if(totalScore >= 70) {			
			grade = "Not bad"; 
		}else if(totalScore >= 60) {			
			grade = "Oops"; 
		}else {			
			grade = "OMG"; 
		}
	}

}
