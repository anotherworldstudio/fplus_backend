package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.SkinScore;

import lombok.Data;

@Data
public class UserSkinView  extends CommonView{
	private Skin skin;
	
	private List<SkinScore> skinScoreList;
	private List<SkinInfo> skinInfoList;	
	
	private int skinRank;
	private int skinAge01;
	private int skinAge02;
	
	private int skinRankPercent;
	
	private int manAge;
	private String sex;
	private String shareLink;
	
	private DoctorSkinView DoctorSkinScore;
}
