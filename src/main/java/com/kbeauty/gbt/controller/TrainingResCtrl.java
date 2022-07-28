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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.SkinResultView;
import com.kbeauty.gbt.entity.view.TrainingCondition;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.UserFaceView;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.entity.view.SaveTraining;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.service.AiService;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.SkinService;
import com.kbeauty.gbt.service.TrainingService;
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
@Api(value = "Training Controller")
@RequestMapping("/v1/training")
@Slf4j
public class TrainingResCtrl {
	
	@Autowired
	private SkinService skinService;
	
	@Autowired
	private TrainingService service;
	
	@ApiOperation(value = "saveTraining", notes = "이미지 분석")
	@RequestMapping(value="/save_training", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	@ResponseBody
	public TrainingView saveTraining(@RequestPart MultipartFile faceImg, @RequestParam String userId, @RequestParam String fileName,
			@RequestParam String skinTone,
			@RequestParam String seasonColor,
			@RequestParam String facialContour,
			@RequestParam String age,
			@RequestParam String gender,
			@RequestParam String lessonId,
			@RequestParam(required = false) String userFaceName,		
			HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception{
		TrainingView view = null;
		Training training = new Training();
		training.setSkinTone(skinTone);
		training.setSeasonColor(seasonColor);
		training.setFacialContour(facialContour);
		training.setAge(age);
		training.setGender(gender);
		training.setLessonId(lessonId);
		training.setUserId(userId);
		try {
			view = service.saveTraining(faceImg, userId, fileName,training,userFaceName);
			view.setOk();
		} catch (MessageException me) {
			view = new TrainingView();
			view.setError(me.getMsg());
		} catch (Exception e) {			
			e.printStackTrace();
			log.error("AI PROCESS ERROR", e);
			view = new TrainingView();
			view.setError(ErrMsg.TRAINING_SAVE);		
		}
		
		return view;
	}

	@ApiOperation(value = "saveTraining2", notes = "이미지 분석")
	@RequestMapping(value="/save_training2", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	@ResponseBody
	public TrainingView saveTraining2( @RequestParam String userId, @RequestParam String fileName,
			@RequestParam String skinTone,
			@RequestParam String seasonColor,
			@RequestParam String facialContour,
			@RequestParam String age,
			@RequestParam String gender,
			@RequestParam String lessonId,
			@RequestParam( required = false) String userFaceName,		
			HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception{
		TrainingView view = null;
		Training training = new Training();
		training.setSkinTone(skinTone);
		training.setSeasonColor(skinTone);
		training.setFacialContour(facialContour);
		training.setAge(age);
		training.setGender(gender);
		training.setLessonId(lessonId);
		training.setUserId(userId);
		try {
			view = service.saveTraining2(userId, fileName,training,userFaceName);
			view.setOk();
		} catch (MessageException me) {
			view = new TrainingView();
			view.setError(me.getMsg());
		} catch (Exception e) {			
			e.printStackTrace();
			log.error("AI PROCESS ERROR", e);
			view = new TrainingView();
			view.setError(ErrMsg.TRAINING_SAVE);		
		}
		
		return view;
	}
	
	
	@ApiOperation(value = "updateTraining", notes = "실습 2단계 정보저장 After사진 저장")
	@RequestMapping(value="/update_training", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")  
	public TrainingView updateTraining(@RequestPart MultipartFile faceImg, @RequestParam String userId, @RequestParam String fileName,
			@RequestParam String trainingId,
		HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception{
		
		TrainingView view = null;
		
		try {
			view = service.updateTraining(faceImg, userId, fileName,trainingId);
			view.setOk();
		} catch (MessageException me) {
			view = new TrainingView();
			view.setError(me.getMsg());
		} catch (Exception e) {			
			e.printStackTrace();
			log.error("AI PROCESS ERROR", e);
			view = new TrainingView();
			view.setError(ErrMsg.TRAINING_SAVE);		
		}
		return view;
	}
	
	@ApiOperation(value = "gradeTraining", notes = "실습 채점 점수메기기")
	@RequestMapping(value="/grade_training", method=RequestMethod.POST)  
	public TrainingView gradeTraining(	@ApiParam(value = "실습정보", required = true)
			@RequestBody Training training,	
			HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception{
		String userId = TokenUtils.getTokenUserId(request);
		TrainingView view = null;
		try {
			view = service.gradeTraining(userId,training);
			view.setOk();
		} catch (MessageException me) {
			view = new TrainingView();
			view.setError(me.getMsg());
		} catch (Exception e) {			
			e.printStackTrace();
			view = new TrainingView();
			view.setError(ErrMsg.TRAINING_SAVE);		
		}
		
		return view;
	}
	
	@ApiOperation(value = "delete", notes = "training 삭제")
	@RequestMapping(value="/delete", method=RequestMethod.POST)	
	public Training delete(HttpServletRequest request, 
			@ApiParam(value = "실습정보", required = true)
	@RequestBody Training training){
		
		if(training == null) {
			training.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return training;
		}
		String trainingId = training.getTrainingId();
		
		if(StringUtil.isEmpty(trainingId)) {
			training.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return training;
		}
		
		String userId = TokenUtils.getTokenUserId(request);
		training = service.deleteTraining(training, userId);		
		training.setOk();
				
		return training;
	}
	
	@ApiOperation(value = "getTrainingListView", notes = "Training 목록 조회함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/list", method=RequestMethod.POST)
	public PagingList<TrainingView> getTrainingListView(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true )
			@RequestBody TrainingCondition condition
			) {
		
		PagingList<TrainingView> list = new PagingList<>();
		if(condition == null) condition = new TrainingCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		String userId = TokenUtils.getTokenUserId(request);
		if(StringUtil.isEmpty(condition.getSearchUserid())) {			
			condition.setSearchUserid(userId);
		}
		
		List<TrainingView> trainingList = service.getTrainingViewList(condition);
		if(trainingList == null || trainingList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getTrainingListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(trainingList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
		
	}
	
	@ApiOperation(value = "getTrainingView", notes = "Training 조회함수")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/{trainingId}", method=RequestMethod.GET)
	public TrainingView getContentView(HttpServletRequest request,			
			@ApiParam(value = "TrainingId", required = false, example = "Content GUID")
			@PathVariable("trainingId") String trainingId
			) { 
		
		String userId = TokenUtils.getTokenUserId(request);
		
		TrainingView trainingView = service.getTrainingViewByTrainingId(trainingId);
		if(trainingView == null) {
			trainingView = new TrainingView();
			trainingView.setError(ErrMsg.NO_RESULT);
			return trainingView;
		}
		
		trainingView.setOk();
		
		return trainingView;
	}
	
	
}