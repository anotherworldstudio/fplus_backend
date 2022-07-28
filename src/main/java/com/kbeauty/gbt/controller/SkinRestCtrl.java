package com.kbeauty.gbt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kbeauty.gbt.entity.AiRawScore;
import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinRanking;
import com.kbeauty.gbt.entity.domain.SkinTypeTest;
import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.DiaryView;
import com.kbeauty.gbt.entity.view.SkinRankingView;
import com.kbeauty.gbt.entity.view.SkinResultView;
import com.kbeauty.gbt.entity.view.SkinTypeView;
import com.kbeauty.gbt.entity.view.UserSimpleSkinView;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.UserSkinView;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.service.AiService;
import com.kbeauty.gbt.service.SkinService;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Skin Controller")
@RequestMapping("/v1/skin")
@Slf4j
public class SkinRestCtrl {

	@Autowired
	private AiService service;

	@Autowired
	private SkinService skinService;

	@ApiOperation(value = "deepSkin", notes = "이미지 분석")
	@RequestMapping(value = "/deep_skin", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public SkinResultView deepSkin(@RequestPart MultipartFile faceImg, @RequestParam String userId,
			@RequestParam String fileName, HttpServletRequest request, RedirectAttributes redirectAttributes,
			HttpServletResponse response) throws Exception {

		SkinResultView skinResultView = null;
		try {
			skinResultView = service.deepSkin(faceImg, userId, fileName);
			skinResultView.setOk();
		} catch (MessageException me) {
			skinResultView = new SkinResultView();
			skinResultView.setError(me.getMsg());
		} catch (Exception e) {
			e.printStackTrace();
			log.error("AI PROCESS ERROR", e);
			skinResultView = new SkinResultView();
			skinResultView.setError(ErrMsg.AI_PROCESS_ERR);
		}

		return skinResultView;
	}

	@ApiOperation(value = "deepSkinWeb", notes = "이미지 분석")
	@RequestMapping(value = "/deep_skin_web", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public SkinResultView deepSkinWeb(@RequestPart MultipartFile faceImg, @RequestParam String userId,
			@RequestParam String fileName, @RequestParam(required = false) String userName, HttpServletRequest request,
			RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception {

		SkinResultView skinResultView = null;
		try {
			skinResultView = service.deepSkin(faceImg, userId, fileName);
			skinResultView.setOk();
		} catch (MessageException me) {
			skinResultView = new SkinResultView();
			skinResultView.setError(me.getMsg());
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			skinResultView = new SkinResultView();
			skinResultView.setError(ErrMsg.AI_PROCESS_ERR);
		}

		return skinResultView;
	}

	@ApiOperation(value = "getSkinResultView", notes = "이미지 평가 내용 조회")
	@RequestMapping(value = "/get_skin_resultview/{userId}", method = RequestMethod.POST)
	public SkinResultView getSkinResultView(
			@ApiParam(value = "사용자ID", required = false, example = "사용자GUID") @PathVariable String userId,
			@RequestBody AiRawScore score) throws Exception {

		SkinResultView skinResultView = null;
		try {
			skinResultView = service.getSkinResultView(score, userId);
			skinResultView.setOk();
		} catch (MessageException me) {
			skinResultView = new SkinResultView();
			skinResultView.setError(me.getMsg());
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			skinResultView = new SkinResultView();
			skinResultView.setError(ErrMsg.AI_PROCESS_ERR);
		}

		return skinResultView;
	}

	@ApiOperation(value = "getMonthSkinData", notes = "해당 월에 대한 피부분석 정보 제공")
	@RequestMapping(value = "/get_month_skindata", method = RequestMethod.POST)
	public PagingList<DiaryView> getMonthSkinData(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody UserSkinCondition condition) throws Exception {

		PagingList<DiaryView> list = new PagingList<>();
		if (condition == null)
			condition = new UserSkinCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);

		int currPage = condition.getPage();
		if (currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		List<DiaryView> contentList = skinService.getMonthSkinData(condition);
		if (contentList == null || contentList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);
			return list;
		}
		paging.setTotalCount(contentList.size());

		list.setList(contentList);
		list.setPaging(paging);
		list.setOk();

		return list;
	}

	@ApiOperation(value = "getSkinResultView", notes = "이미지 평가 내용 조회")
	@RequestMapping(value = "/get_daily_skindata", method = RequestMethod.POST)
	public DiaryView getDailySkinData(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody UserSkinCondition condition) throws Exception {
		DiaryView diaryView = null;

		try {
			diaryView = skinService.getDailySkinData(condition);
			diaryView.setOk();
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			diaryView = new DiaryView();
			diaryView.setError(ErrMsg.NO_RESULT);
		}

		return diaryView;
	}

	@ApiOperation(value = "getSkinRanking", notes = "스킨 랭킹 조회")
	@RequestMapping(value = "/get_skin_ranking", method = RequestMethod.POST)
	public SkinRankingView getSkinRanking(HttpServletRequest request, @RequestBody SkinRankingView view)
			throws Exception {

		SkinRankingView skinRankingView = null;
		List<SkinRanking> list;

		try {
			skinRankingView = view;
			list = skinService.getSkinRankingBasic(view);
			skinRankingView.setRankingList(list);
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			System.out.println(e.toString());
			skinRankingView = new SkinRankingView();
			skinRankingView.setError(ErrMsg.SKIN_NOT_RANKING);
		}
		skinRankingView.setOk();

		return skinRankingView;
	}

	@ApiOperation(value = "getTermSkinData", notes = "기간별 피부 분석 정보 조회")
	@RequestMapping(value = "/get_term_skin_data", method = RequestMethod.POST)
	public DiaryView getTermSkinData(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody UserSkinCondition condition) throws Exception {
		DiaryView diaryView = null;

		try {
			diaryView = skinService.getTermDailySkinData(condition);
			diaryView.setOk();
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
			diaryView = new DiaryView();
			diaryView.setError(ErrMsg.NO_RESULT);
		}

		return diaryView;
	}

	@ApiOperation(value = "getSimpleSkin", notes = "최근 5일 스킨정보")
	@RequestMapping(value = "/get_simple_skin", method = RequestMethod.POST)
	public UserSimpleSkinView getFiveDaySkin(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestParam String userId) throws Exception {

		UserSimpleSkinView view = new UserSimpleSkinView();
		List<Skin> skinList = new ArrayList<Skin>();

		try {
			skinList = skinService.getSimpleSkin(userId);
			view.setSkinList(skinList);
			view.setOk();
		} catch (Exception e) {
			log.error("SIMPLE SKIN ERROR", e);
			view.setSkinList(new ArrayList<>());
			view.setError(ErrMsg.SIMPLE_SKIN_ERROR);
		}

		return view;
	}

	@ApiOperation(value = "delAndNewSkinTypeTest", notes = "이어서하기 포기")
	@RequestMapping(value = "/del_and_new_skin_type_test", method = RequestMethod.POST)
	public SkinTypeView delAndNewSkinTypeTest(HttpServletRequest request,
			@ApiParam(value = "저장", required = true) @RequestBody SkinTypeTest test) throws Exception {
		SkinTypeView view = new SkinTypeView();
		String userId = TokenUtils.getTokenUserId(request);
		try {
			SkinTypeTest returnTest = skinService.delAndNewSkinTypeTest(test, userId);
			view.setSkinTypeTest(returnTest);
			view.setOk();
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SKIN_TYPE_TEST_ERROR);
		}

		return view;
	}

	@ApiOperation(value = "createSkinTypeTest", notes = "테스트 정보 만들기")
	@RequestMapping(value = "/create_skin_type_test", method = RequestMethod.POST)
	public SkinTypeView createSkinTypeTest(HttpServletRequest request) throws Exception {
		SkinTypeView view = new SkinTypeView();
		String userId = TokenUtils.getTokenUserId(request);
		try {
			SkinTypeTest returnTest = skinService.createSkinTypeTest(userId);
			view.setSkinTypeTest(returnTest);
			view.setOk();
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SKIN_TYPE_TEST_ERROR);
		}
		return view;
	}

	@ApiOperation(value = "getSkinTypeTest", notes = "스킨타입 테스트 조회&검사")
	@RequestMapping(value = "/get_skin_type_test", method = RequestMethod.POST)
	public SkinTypeView getSkinTypeView(HttpServletRequest request) throws Exception {
		SkinTypeView view = null;
		String userId = TokenUtils.getTokenUserId(request);

		try {
			view = skinService.getSkinTypeTest(userId);
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SKIN_TYPE_TEST_ERROR);
		}

		return view;
	}

	@ApiOperation(value = "saveSkinTypeTestStep", notes = "스킨타입 테스트 현재 정보 저장")
	@RequestMapping(value = "/save_skin_type_test_step", method = RequestMethod.POST)
	public SkinTypeView saveSkinTypeTestStep(HttpServletRequest request,
			@ApiParam(value = "저장", required = true) @RequestBody SkinTypeTestStep step) throws Exception {

		SkinTypeView view = new SkinTypeView();
		String userId = TokenUtils.getTokenUserId(request);
		try {
			SkinTypeTestStep returnStep = skinService.saveSkinTypeTestStep(step, userId);
			SkinTypeTest test = skinService.getTestByTestId(step.getTestId());
			view.setSkinTypeTest(test);
			view.setStep(returnStep);
			view.setOk();
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SAVE_SKIN_TYPE_TEST_STEP_ERROR);
		}

		return view;
	}

	@ApiOperation(value = "getSkinTypeTestStep", notes = "스킨타입 테스트 특정문제항목 정보 뽑아오기")
	@RequestMapping(value = "/get_skin_type_test_step", method = RequestMethod.POST)
	public SkinTypeView getSkinTypeTestStep(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody SkinTypeTestStep step) throws Exception {
		SkinTypeView view = new SkinTypeView();
		String userId = TokenUtils.getTokenUserId(request);
		try {
			SkinTypeTestStep returnStep = skinService.getSkinTypeTestStep(step, userId);
			SkinTypeTest test = skinService.getTestByTestId(step.getTestId());
			view.setSkinTypeTest(test);
			view.setStep(returnStep);
			view.setOk();
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SAVE_SKIN_TYPE_TEST_STEP_ERROR);
		}

		return view;
	}

	@ApiOperation(value = "setSkinTypeTestStep", notes = "문항별 답안 저장 & 조회")
	@RequestMapping(value = "/set_skin_type_test_step", method = RequestMethod.POST)
	public SkinTypeView setSkinTypeTestStep(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody SkinTypeTestStep step) throws Exception {
		SkinTypeView view = new SkinTypeView();
		String userId = TokenUtils.getTokenUserId(request);
		try {
			view = skinService.saveSkinTypeTestStep2(step, userId);
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SET_SKIN_TYPE_TEST_STEP_ERROR);
		}

		return view;
	}

	@ApiOperation(value = "getSkinTypeTestResult", notes = "스킨타입테스트 결과")
	@RequestMapping(value = "/get_skin_type_test_result", method = RequestMethod.POST)
	public SkinTypeView getSkinTypeTestResult(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true) @RequestBody SkinTypeTest test) throws Exception {
		SkinTypeView view = new SkinTypeView();
		String userId = TokenUtils.getTokenUserId(request);
		try {
			view = skinService.getSkinTypeTestResult(test, userId);
			view.setOk();
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
			view = new SkinTypeView();
			view.setError(ErrMsg.SAVE_SKIN_TYPE_TEST_STEP_ERROR);
		}
		return view;
	}

	// 개발자 테스트용 함수
	@ApiOperation(value = "deleteSkinTypeTestAll", notes = "스킨타입테스트결과 전부지우기")
	@RequestMapping(value = "/delete_skin_type_test_all", method = RequestMethod.POST)
	public void deleteSkinTypeTestAll(HttpServletRequest request) throws Exception {

		String userId = TokenUtils.getTokenUserId(request);
		try {
			skinService.deleteSkinTypeTestAll(userId);
		} catch (Exception e) {
			log.error("SKINTYPETEST", e);
		}

	}

}