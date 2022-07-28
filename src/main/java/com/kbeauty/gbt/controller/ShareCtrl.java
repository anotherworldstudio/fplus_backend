package com.kbeauty.gbt.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.kbeauty.gbt.dao.SkinScoreRepo;
import com.kbeauty.gbt.dao.UserRepo;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinScore;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.SkinArea;
import com.kbeauty.gbt.entity.view.SkinShareView;
import com.kbeauty.gbt.entity.view.UserSkinView;
import com.kbeauty.gbt.service.SkinService;
import com.kbeauty.gbt.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1")
@Controller
@Slf4j
public class ShareCtrl {

	@Autowired
	SkinService service;
	
	@Autowired
	SkinScoreRepo skinScoreRepo;
	
	@Autowired
	UserRepo userRepo;

	
//	@RequestMapping(value="/skin_share/{skinid}", method=RequestMethod.GET)
//	public String getContentView(			
//			@PathVariable("skinid") String skinId,
//			Model model
//			) {
//		
//		UserSkinView userSkinView = service.getUserSkinView(skinId);
//		
//		if(userSkinView == null) {
//			userSkinView = new UserSkinView();
//			userSkinView.setError(ErrMsg.NO_RESULT);
//			return "";
//		}
//		
//		SkinShareView skinShareView = new SkinShareView();
//		Skin skin = userSkinView.getSkin();
//		
//		skinShareView.setSkinid(skin.getSkinId());
//		skinShareView.setUserId(skin.getUserId());
//		skinShareView.setUserName(skin.getUserName());
//		
//		String dDay = skin.getDDay();
//		// 2021.02.25 AI 피부 분석 결과
//		String year = dDay.substring(0,4);
//		String month = dDay.substring(4,6);
//		String day = dDay.substring(6,8);
//		StringBuffer sb = new StringBuffer();
//		sb.append(year).append(".");
//		sb.append(month).append(".");
//		sb.append(day).append(" AI 피부 분석 결과");
//			
//		skinShareView.setTitle(sb.toString());
//		
//		skinShareView.setComment(skin.getComment());		
//		skinShareView.setUpperPer(userSkinView.getSkinRankPercent());		
//		skinShareView.setSkinAge(userSkinView.getSkinAge01());
//		skinShareView.setTotalScore(skin.getTotalScore());
//				
//		List<SkinScore> skinScoreList = userSkinView.getSkinScoreList();
//		String code = null;		
//		int wrinkle = 0;
//		int pigment = 0;
//		int trouble = 0;
//		int pore = 0;
//		int redness = 0;
//		
//		for (SkinScore skinScore : skinScoreList) {
//			code = skinScore.getItemCode();
//			if(SkinArea.WRINKLE.getCode().equals(code)) {
//				wrinkle= skinScore.getAdjustScore();				
//			}else if(SkinArea.PORE.getCode().equals(code)) {
//				pore= skinScore.getAdjustScore();
//			}else if(SkinArea.TROUBLE.getCode().equals(code)) {				
//				trouble= skinScore.getAdjustScore();				
//			}else if(SkinArea.PIGMENT.getCode().equals(code)) {
//				pigment= skinScore.getAdjustScore();
//			}else if(SkinArea.REDNESS.getCode().equals(code)) {				
//				redness= skinScore.getAdjustScore();
//			}
//		}
//		
//		skinShareView.setAiUrl(skin.getAiUrl());
//		skinShareView.setWrinkle(wrinkle);
//		skinShareView.setPigment(pigment);
//		skinShareView.setTrouble(trouble);
//		skinShareView.setPore(pore);
//		skinShareView.setRedness(redness);
//		
//		model.addAttribute("skinShareView", skinShareView);
//		
//		return "share/skin_share";
//	}
	
	@RequestMapping(value="/skin_share/{skinid}", method=RequestMethod.GET)
	public String getContentView(			
			@PathVariable("skinid") String skinId,
			Model model
			) {
		
		UserSkinView userSkinView = service.getUserSkinView(skinId);
		
		if(userSkinView == null) {
			userSkinView = new UserSkinView();
			userSkinView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		
		SkinShareView skinShareView = new SkinShareView();
		Skin skin = userSkinView.getSkin();
		User user = userRepo.findByUserId(skin.getUserId());
		int age = DateUtil.getManAge(user.getBirthDay());
		skinShareView.setRealAge(age);
		
		List<SkinScore> commentList = skinScoreRepo.findBySkinId(skin.getSkinId());
		for (SkinScore skinScore : commentList) {
			if(skinScore.getItemCode().equals(SkinArea.PIGMENT.getCode())) {
				skinShareView.setPigmentComment(skinScore.getComment());
			}
			else if(skinScore.getItemCode().equals(SkinArea.PORE.getCode())) {
				skinShareView.setPoreComment(skinScore.getComment());
			}
			else if(skinScore.getItemCode().equals(SkinArea.REDNESS.getCode())) {
				skinShareView.setRednessComment(skinScore.getComment());
			}
			else if(skinScore.getItemCode().equals(SkinArea.TROUBLE.getCode())) {
				skinShareView.setTroubleComment(skinScore.getComment());
			}
			else if(skinScore.getItemCode().equals(SkinArea.WRINKLE.getCode())) {
				skinShareView.setWrinkleComment(skinScore.getComment());
			}
		}           
	 
		
		skinShareView.setSkinid(skin.getSkinId());
		skinShareView.setUserId(skin.getUserId());
		skinShareView.setUserName(skin.getUserName());
		
		String dDay = skin.getDDay();
		// 2021.02.25 AI 피부 분석 결과
		String year = dDay.substring(0,4);
		String month = dDay.substring(4,6);
		String day = dDay.substring(6,8);
		StringBuffer sb = new StringBuffer();
		sb.append(year).append(".");
		sb.append(month).append(".");
		sb.append(day).append(" AI 피부 분석 결과");
			
		skinShareView.setTitle(sb.toString());
		
		skinShareView.setComment(skin.getComment());
		skinShareView.setUpperPer(userSkinView.getSkinRankPercent());
		skinShareView.setSkinAge(userSkinView.getSkinAge01());
		skinShareView.setTotalScore(skin.getTotalScore());
				
		List<SkinScore> skinScoreList = userSkinView.getSkinScoreList();
		String code = null;		
		int wrinkle = 0;
		int pigment = 0;
		int trouble = 0;
		int pore = 0;
		int redness = 0;
		
		for (SkinScore skinScore : skinScoreList) {
			code = skinScore.getItemCode();
			if(SkinArea.WRINKLE.getCode().equals(code)) {
				wrinkle= skinScore.getAdjustScore();				
			}else if(SkinArea.PORE.getCode().equals(code)) {
				pore= skinScore.getAdjustScore();
			}else if(SkinArea.TROUBLE.getCode().equals(code)) {
				trouble= skinScore.getAdjustScore();				
			}else if(SkinArea.PIGMENT.getCode().equals(code)) {
				pigment= skinScore.getAdjustScore();
			}else if(SkinArea.REDNESS.getCode().equals(code)) {
				redness= skinScore.getAdjustScore();
			}
		}
		  
		skinShareView.setAiUrl(skin.getAiUrl());
		skinShareView.setWrinkle(wrinkle);
		skinShareView.setPigment(pigment);
		skinShareView.setTrouble(trouble);
		skinShareView.setPore(pore);
		skinShareView.setRedness(redness);
				
		
		model.addAttribute("skinShareView", skinShareView);
		
		return "share/skin_share2";
	}
	
	
}
