package com.kbeauty.gbt.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.LessonFace;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.enums.LessonFaceType;
import com.kbeauty.gbt.entity.enums.LessonType;
import com.kbeauty.gbt.entity.enums.SeasonColor;
import com.kbeauty.gbt.entity.enums.SkinTone;
import com.kbeauty.gbt.entity.enums.AgeGroup;
import com.kbeauty.gbt.entity.enums.CodeVal;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FacialContour;
import com.kbeauty.gbt.entity.enums.Gender;
import com.kbeauty.gbt.entity.enums.LessonCategory1;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.LessonCondition;
import com.kbeauty.gbt.entity.view.LessonView;
import com.kbeauty.gbt.entity.view.PointCondition;
import com.kbeauty.gbt.entity.view.TrainingCondition;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.LessonCondition;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.WeatherDetailView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.service.LessonService;
import com.kbeauty.gbt.service.TrainingService;
import com.kbeauty.gbt.service.LessonService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/training")
@Controller
@Slf4j
public class TrainingCtrl {

	@Autowired
	TrainingService service;

	@Resource
	private Login login;
	
	private final static String conditionKey = "TrainingCondition";
	
	private void setCondition(HttpServletRequest request, TrainingCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}
	
	private TrainingCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		TrainingCondition condiiton = (TrainingCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	private TrainingCondition getDefaultContentCondition() {
		TrainingCondition condition = new TrainingCondition();
		
		return condition;
	}
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, TrainingCondition condition, Model model) {		
		
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new TrainingCondition();
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = new TrainingCondition();
				}		
			}
		}
		model.addAttribute("condition", condition);
		
		return "admin/training/list";
	}
	
	private void goListCommon(HttpServletRequest request, TrainingCondition condition, Model model) {
		String reset = (String)request.getParameter("reset");
		
		if(YesNo.isYes(reset)) {
			condition = getDefaultContentCondition();						
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = getDefaultContentCondition();
				}		
			}
		}
		
		model.addAttribute("condition", condition);
	}			

	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<TrainingView> getLessonListView(HttpServletRequest request, @RequestBody TrainingCondition condition) {
		
		PagingList<TrainingView> list = new PagingList<>();
		if(condition == null) condition = new TrainingCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		setCondition(request, condition);
			
		String userId = login.getUserId();
		condition.setSearchUserid(userId);
		
		List<TrainingView> LessonList = service.getTrainingViewList(condition);
		if(LessonList == null || LessonList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getTrainingListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(LessonList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
	}	
//	
//	@RequestMapping(value="/delete_mainimg")
//	@ResponseBody
//	public Lesson deleteManiImg(HttpServletRequest request, @RequestBody Lesson Lesson) {
//		
//		if(Lesson == null) {			
//			Lesson.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
//			return Lesson;
//		}
//		String LessonId = Lesson.getLessonId();
//		
//		if(StringUtil.isEmpty(LessonId)) {
//			Lesson.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
//			return Lesson;
//		}
//		
//		String userId = login.getUserId();
//		
//		//기존 Lesson를 가지고 온다.
//		Lesson dbLesson = service.getLesson(LessonId);
//		dbLesson.setMainDir("");
//		dbLesson.setMainFileName("");
//		dbLesson.setMainUrl("");
//		
//		Lesson = service.saveLesson(dbLesson, userId);		
//		Lesson.setOk();
//		
//		
//		return Lesson;
//	}	
	
	@RequestMapping(value="/detail/{trainingId}", method=RequestMethod.GET)
	public String getTrainingView(
			@PathVariable("trainingId") String trainingId,
			Model model
			) { 
		
//		String userId = login.getUserId();
		TrainingView trainingView = service.getTrainingViewByTrainingId(trainingId);
		if(trainingView == null) {
			trainingView = new TrainingView();
			trainingView.setError(ErrMsg.NO_RESULT);
			return "";
		}
	
		trainingView.setOk();
		
		model.addAttribute("trainingView", trainingView);
		
		return "admin/training/detail";
	}
	
	@RequestMapping(value="/save_training", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String save(Training training , @RequestParam("mainUploadFile") MultipartFile mainFile) throws Exception{
		
		if(training == null){	
			training.setError(ErrMsg.LESSON_NO_SAVE_ERR);
			return "error";
		}
		String trainingId = training.getTrainingId();
		
		if(StringUtil.isEmpty(trainingId)) {
			training.setError(ErrMsg.LESSON_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();
		//
		
		//기존 Lesson를 가지고 온다.
		Training dbTraining = service.getTraining(trainingId);
//		dbTraining.setTitle       (Lesson.getTitle       ());
//		dbTraining.setContent     (Lesson.getContent     ());
//		dbTraining.setViewType    (Lesson.getViewType    ());
//		dbTraining.setStartDate   (Lesson.getStartDate   ());
//		dbTraining.setEndDate     (Lesson.getEndDate     ());		
//		dbTraining.setStatus      (Lesson.getStatus      ());
		
		training = service.saveTraining(dbTraining, userId);
		training.setOk();		
		
		String nextPage = "redirect:/w1/training/detail/" + training.getTrainingId();
		return nextPage;
	}
	
	@RequestMapping(value="/delete_training", method=RequestMethod.POST)
	public String delete(Training training){
		
		if(training == null) {
			training = new Training();
			training.setError(ErrMsg.TRAINING_DELETE_ERR);
			return "error";
		}
		
		String trainingId = training.getTrainingId();
		
		if(StringUtil.isEmpty(trainingId)) {
			training.setError(ErrMsg.TRAINING_DELETE_ERR);
			return "error";
		}
		String userId = login.getUserId();
		training = service.deleteTraining(training, userId);		
		training.setOk();
				
		String nextPage = "redirect:/w1/training/go_list";
		return nextPage;
		
	}
	
	@RequestMapping(value="/grade_training", method=RequestMethod.POST)  
	public String gradeTraining(Training training,
								HttpServletRequest request) throws Exception{
		
		String userId = login.getUserId();
		
		if(training == null){
			training.setError(ErrMsg.LESSON_NO_SAVE_ERR);
			return "error";
		}
		
		String trainingId = training.getTrainingId();
		if(StringUtil.isEmpty(trainingId)){
			training.setError(ErrMsg.LESSON_NO_SAVE_ERR);
			return "error";
		}
		
		service.gradeTraining(userId,training);
		training.setOk();
			
		String nextPage = "redirect:/w1/training/detail/" + training.getTrainingId();
		
		return nextPage;
	}
	
	
	@GetMapping("/go_evaluate_popup")
	public String goEvaluatePopup(HttpServletRequest request, TrainingCondition condition, Model model,@RequestParam(required = false)String trainingId) {		
		goListCommon(request, condition, model);
		condition  =  (TrainingCondition) model.getAttribute("condition");
		if(trainingId!=null&&!"".equals(trainingId)) {
			condition.setTrainingId(trainingId);
			model.addAttribute("condition", condition);
		}
		return "admin/training/evaluate_popup";
	}
	
}
