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
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.NoticeView;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.service.WeatherService;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Notice Controller")
@RequestMapping("/v1/notice")
@Slf4j
public class NoticeResCtrl {

	@Autowired
	private ContentService service;

	// 공지 리스트 뽑아오기 (타입별)
	@RequestMapping(value = "/getNotice_Popup", method = RequestMethod.POST)
	public NoticeView getNoticePopup(HttpServletRequest request, String noticeType ) {
		NoticeView view;
		try {
			view = new NoticeView();
			List<ContentView> noticeList = service.getNotice(noticeType);
			view.setNoticeList(noticeList);
			view.setCount(noticeList.size());
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			view = new NoticeView();
			view.setError(ErrMsg.NO_RESULT);
			return view;
		}
		view.setOk();
		return view;
	}
	
	// 공지 리스트 뽑아오기(타입배열) 
	@RequestMapping(value = "/getNoticeArray_Popup", method = RequestMethod.POST)
	public NoticeView getNoticePopup(HttpServletRequest request, String[] noticeType ) {
		NoticeView view;
		try {
			view = new NoticeView();
			List<ContentView> noticeList = service.getNotice(noticeType);
			view.setNoticeList(noticeList);
			view.setCount(noticeList.size());
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			view = new NoticeView();
			view.setError(ErrMsg.NO_RESULT);
			return view;
		}
		view.setOk();
		return view;
	}

	// 공지리스트 전부가져오기
		@RequestMapping(value = "/getNoticeAll", method = RequestMethod.POST)
		public NoticeView getNoticeAll(HttpServletRequest request) {
			NoticeView view;
			try {
				view = new NoticeView();
				List<ContentView> noticeList = service.getNoticeAll();
				view.setNoticeList(noticeList);
				view.setCount(noticeList.size());
			} catch (Exception e) {
				log.error("AI PROCESS ERROR", e);
				view = new NoticeView();
				view.setError(ErrMsg.NO_RESULT);
				return view;
			}
			view.setOk();
			return view;
		}
		
	
}