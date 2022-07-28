package com.kbeauty.gbt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.GameData;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.GameView;
import com.kbeauty.gbt.entity.view.PointView;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.PointService;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.service.WeatherService;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "Point Controller")
@RequestMapping("/v1/point")
public class PointResCtrl {

	@Autowired
	private PointService service;

//	@RequestMapping(value="/recommend", method=RequestMethod.POST)
//	public PointView recommendTest( HttpServletRequest request,
//			@RequestBody PointView inputPointView){
//
//		String userId = TokenUtils.getTokenUserId(request);
//		PointView ptView = null;
//		User recommendUser = service.findUserByNickname(inputPointView.getRecommendUserName());
//
//		if (recommendUser == null) { // 상대 닉네임 검색 결과없으면 errMsg리턴
//			ptView = new PointView();
//			ptView.setError(ErrMsg.NO_RECOMMEND_USER);
//			return ptView;
//		}
//		
//		ptView.setOk();
//
//		return ptView;
//	}

	@RequestMapping(value = "/update_mileage", method = RequestMethod.POST)
	public PointView updateMileage(HttpServletRequest request, @RequestBody Mileage mileage) {
		
		String userId = TokenUtils.getTokenUserId(request);
		PointView ptView = service.updateMileage(mileage, userId);

		if (ptView == null) {
			ptView = new PointView();
			ptView.setError(ErrMsg.POINT_ERROR);
			return ptView;
		}

		return ptView;
	}

	@RequestMapping(value = "/get_have_mileage", method = RequestMethod.POST)
	public int getHaveMileage(HttpServletRequest request, @RequestBody Mileage mileage) {

		String userId = TokenUtils.getTokenUserId(request);
		int haveMileage = service.getHaveMileage(mileage.getUserId());

		return haveMileage;
	}

	@RequestMapping(value = "/update_exp", method = RequestMethod.POST)
	public PointView updateExp(HttpServletRequest request, @RequestBody Exp exp) {

		String userId = TokenUtils.getTokenUserId(request);
		PointView ptView = service.updateExp(exp, userId);

		if (ptView == null) {
			ptView = new PointView();
			ptView.setError(ErrMsg.POINT_ERROR);
			return ptView;
		}

		return ptView;
	}

	@RequestMapping(value = "/get_have_exp", method = RequestMethod.POST)
	public int getHaveExp(HttpServletRequest request, @RequestBody Exp exp) {

		String userId = TokenUtils.getTokenUserId(request);
		int haveExp = service.getHaveExp(exp.getUserId());

		return haveExp;
	}

	@RequestMapping(value = "/get_game_data", method = RequestMethod.POST)										
	public GameView getGameData(HttpServletRequest request) {
		GameView view = new GameView();
		try {
			String userId = TokenUtils.getTokenUserId(request);
			GameData data = service.getGameData(userId);
			if (data != null) {
				view.setGameData(data);
			} else {
				view.setError(ErrMsg.GAME_DATA);
				return view;
			}
		} catch (Exception E) {
			view.setError(ErrMsg.GAME_DATA);
			return view;
		} 	
		view.setOk();
		return view;
	}

	@RequestMapping(value = "/go_next_stage", method = RequestMethod.POST)
	public GameView goNextStage(HttpServletRequest request) {
		GameView view = new GameView();
		try {
			String userId = TokenUtils.getTokenUserId(request);
			GameData data = service.goNextStage(userId);
			if (data != null) {
				view.setGameData(data);
			} else {
				view.setError(ErrMsg.GAME_DATA);
				return view;
			}
		} catch (Exception E) {
			view.setError(ErrMsg.GAME_DATA);
			return view;
		}
		view.setOk();
		return view;
	}
	
}
