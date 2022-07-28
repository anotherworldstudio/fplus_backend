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

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.UserFaceWho;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.LessonCondition;
import com.kbeauty.gbt.entity.view.LessonView;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.LessonService;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.service.WeatherService;
import com.kbeauty.gbt.util.StringUtil;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Lesson Controller")
@RequestMapping("/v1/lesson")
@Slf4j
public class LessonResCtrl {

	@Autowired
	private LessonService service;

	@ApiOperation(value = "save", notes = "lesson 저장 주는 그대로")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(Lesson lesson) {
		service.save(lesson);
	}

	@ApiOperation(value = "getLessonView", notes = "Lesson 조회함수")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!") })
	@RequestMapping(value = "/{lessonId}", method = RequestMethod.GET)
	public LessonView getContentView(HttpServletRequest request,
			@ApiParam(value = "LessonId", required = false, example = "Lesson GUID") @PathVariable("lessonId") String lessonId) {

		String userId = TokenUtils.getTokenUserId(request);
		LessonView lessonView = service.getLessonView(lessonId, userId);
		if (lessonView == null) {
			lessonView = new LessonView();
			lessonView.setError(ErrMsg.NO_RESULT);
			return lessonView;
		}
		lessonView.setOk();
		return lessonView;
	}

	@ApiOperation(value = "delete", notes = "lesson 삭제")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Lesson delete(HttpServletRequest request, @RequestBody Lesson lesson) {

		if (lesson == null) {
			lesson.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return lesson;
		}
		String lessonId = lesson.getLessonId();

		if (StringUtil.isEmpty(lessonId)) {
			lesson.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return lesson;
		}

		String userId = TokenUtils.getTokenUserId(request);
		lesson = service.deleteLesson(lesson, userId);
		lesson.setOk();

		return lesson;
	}

	@ApiOperation(value = "getLessonListView", notes = "Lesson 목록 조회함수")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!") })
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public PagingList<LessonView> getContentListView(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody LessonCondition condition) {
		PagingList<LessonView> list = new PagingList<>();

		try {
			if (condition == null)
				condition = new LessonCondition();
			Paging paging = new Paging();
			paging.setCondition(condition);

			int currPage = condition.getPage();
			if (currPage == 0) {
				currPage = 1;
			}

			paging.setPage(currPage);

			String userId = TokenUtils.getTokenUserId(request);

			condition.setSearchUserid(userId);
			condition = service.checkUserFace(condition);

			List<LessonView> lessonList = service.getLessonViewList(condition);

			if (lessonList == null || lessonList.isEmpty()) {
				list.setError(ErrMsg.NO_RESULT);
				return list;
			}

			int totalCnt = service.getLessonListCnt(condition);
			paging.setTotalCount(totalCnt);

			list.setList(lessonList);
			list.setPaging(paging);
			list.setOk();

		} catch (Exception e) {
			e.printStackTrace();
			log.error("AI PROCESS ERROR", e);
			list.isNotOk();
		}

		return list;
	}

}