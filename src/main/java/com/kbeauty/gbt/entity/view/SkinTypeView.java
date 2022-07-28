package com.kbeauty.gbt.entity.view;

import java.util.List;

import javax.persistence.Transient;

import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.SkinScore;
import com.kbeauty.gbt.entity.domain.SkinTypeTest;
import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;
import com.kbeauty.gbt.entity.domain.User;

import lombok.Data;

@Data
public class SkinTypeView  extends CommonView{
	
	private User user;
	private SkinTypeTest skinTypeTest;
	private List<SkinTypeTestStep> stepList;
	private SkinTypeTestStep step;

	private float score;
	private float odScore;
	private float srScore;
	private float pnScore;
	private float wtScore;
	
	
	private String status;  // 2000 다한사람 1000 하다만사람 0000 안한사람
	private int finalStep;  // 마지막으로 한곳
	
	public void setAllScore(SkinTypeView view) {
		   score = view.getScore();
		 odScore = view.getOdScore();
		 srScore = view.getSrScore();
		 pnScore = view.getPnScore();
		 wtScore = view.getWtScore();
		 
	}
	

}
