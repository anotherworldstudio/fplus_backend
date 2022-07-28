package com.kbeauty.gbt.entity.view;

import java.util.List;

import javax.persistence.Transient;

import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.domain.UserSkin;

import lombok.Data;

@Data
public class UserView extends CommonView{
	private User user;
	private UserSkin userSkin;
	private Follow follow;
	private UserFace userFace;
	private List<TrainingView> trainingList;
	
	private int followingCnt;
	private int followCnt;
	private int likeCnt;
	private int totalMileage;
	private int totalExp;
}
