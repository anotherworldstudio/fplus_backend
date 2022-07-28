package com.kbeauty.gbt.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
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

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.LessonFace;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Resources;
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
import com.kbeauty.gbt.entity.view.LessonCondition;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.WeatherDetailView;
import com.kbeauty.gbt.service.LessonService;
import com.kbeauty.gbt.service.LessonService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/lesson")
@Controller
@Slf4j
public class LessonCtrl {

	@Autowired
	LessonService service;

	@Resource
	private Login login;
	
	private final static String conditionKey = "LessonCondition";
	
	private void setCondition(HttpServletRequest request, LessonCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}
	
	private LessonCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		LessonCondition condiiton = (LessonCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, LessonCondition condition, Model model) {		
		
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new LessonCondition();
//			condition.setLessonType(LessonType.FEED.getCode());
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = new LessonCondition();
//					condition.setLessonType(LessonType.FEED.getCode());
				}		
			}
		}
		model.addAttribute("condition", condition);
		
		return "admin/lesson/list";
		
	}
	
	@GetMapping("/go_create")
	public String goCreate() {
		return "admin/lesson/create";
	}
	
	
	@RequestMapping(value="/create", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String create(
			Lesson Lesson, 
			@RequestParam("mainUploadFile") MultipartFile mainFile,
			@RequestParam("fileName") List<MultipartFile> files,
			@RequestParam("resourceType") List<String> resourceType,
			@RequestParam("resourceTitle") List<String> resourceTitle,
			@RequestParam("resourceContent") List<String> resourceContent,
			@RequestParam("url") List<String> url,
			@RequestParam("resourceCategory") List<String> category,
			Model model
			) throws Exception{
		List<Resources> list = new ArrayList<>();
//		Resources res = null;
//		
//		for (MultipartFile file : files) {
//			log.debug(file.getName());
//		}
		
		String userId = login.getUserId();
		
		String LessonId = service.getLessonId();
//		long LessonSeq = service.getLessonSeq();
		Lesson.setLessonId(LessonId);
//		Lesson.setSeq(LessonSeq);
		Lesson.setOwnerId(userId);
		
		//Web에서 첫번째 행은 널 값을 가지고 온다.
		if(! StringUtil.isEmpty(resourceContent) && resourceContent.size() > 1) {			
			files.remove(0);
			url.remove(0);
			resourceType.remove(0);
			resourceContent.remove(0);
			resourceTitle.remove(0);
			category.remove(0);
			list = service.createFiles(files, userId, LessonId, resourceTitle, resourceContent, url, category);
		}
		
		if( ! FileUtil.isEmpty(mainFile)) {
			service.createLessonFile(mainFile, userId, Lesson);
		}
		
		LessonView view = new LessonView();
		view.setLesson(Lesson);
		view.setResourceList(list);
		
		service.create(view);

		
		String nextPage = "redirect:/w1/lesson/detail/" + LessonId;
//		nextPage = "https://gbt.beautej.com/w1/lesson/detail/" + LessonId;
		return nextPage;	
	}
	

	@RequestMapping(value="/list")	
	@ResponseBody
	public PagingList<LessonView> getLessonListView(HttpServletRequest request, @RequestBody LessonCondition condition) {
		
		PagingList<LessonView> list = new PagingList<>();
		if(condition == null) condition = new LessonCondition();
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
		
		// copy 실행시 복제생성후 시작
		if(condition.getCopyLessonId()!=null && !"".equals(condition.getCopyLessonId())) {
			 service.copyLesson(condition.getCopyLessonId(),userId);
		}
		
		List<LessonView> LessonList = service.getLessonViewList(condition);
		if(LessonList == null || LessonList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getLessonListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(LessonList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
	}	
	
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
	
	@RequestMapping(value="/detail/{lessonId}", method=RequestMethod.GET)
	public String getLessonView(
			@PathVariable("lessonId") String lessonId,
			Model model
			) { 
		
		String userId = login.getUserId();
		
		LessonView LessonView = service.getLessonView(lessonId);
		if(LessonView == null) {
			LessonView = new LessonView();
			LessonView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		LessonView.setOk();
		
		model.addAttribute("lessonView", LessonView);
		
		return "admin/lesson/detail";
	}
	
	@RequestMapping(value="/save_lesson", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String save(Lesson lesson) throws Exception{
		
		if(lesson == null) {			
			lesson.setError(ErrMsg.LESSON_NO_SAVE_ERR);
			return "error";
		}
		String LessonId = lesson.getLessonId();
		
		if(StringUtil.isEmpty(LessonId)) {
			lesson.setError(ErrMsg.LESSON_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();
		//
		
		//기존 Lesson를 가지고 온다.
		Lesson dbLesson = service.getLesson(LessonId);
		dbLesson.setTitle       (lesson.getTitle       ());
		dbLesson.setContent     (lesson.getContent     ());
		dbLesson.setViewType    (lesson.getViewType    ());
		dbLesson.setStartDate   (lesson.getStartDate   ());
		dbLesson.setEndDate     (lesson.getEndDate     ());		
		dbLesson.setActive      (lesson.getActive      ());
		dbLesson.setStatus      (lesson.getStatus      ());
		dbLesson.setOrders      (lesson.getOrders      ());
		dbLesson.setMainLink    (lesson.getMainLink    ());
		dbLesson.setCategory1(lesson.getCategory1());
		if(LessonCategory1.ALL.getCode().equals(lesson.getCategory1())) {
		}else {
			dbLesson.setCategory2(lesson.getCategory2());
		}
		dbLesson.setOrders(lesson.getOrders());
		
//		if( ! FileUtil.isEmpty(mainFile)) {
//		    service.createLessonFile(mainFile, userId, dbLesson);
//		}
		
		lesson = service.saveLesson(dbLesson, userId);
		lesson.setOk();		
		
		String nextPage = "redirect:/w1/lesson/detail/" + lesson.getLessonId();
		return nextPage;
	}
	
	@RequestMapping(value="/set_lesson_face_category")
	@ResponseBody
	public List<LessonFace> setLessonFaceCategory(HttpServletRequest request, @RequestBody LessonFace face) {
		List<LessonFace> list = new ArrayList<LessonFace>();
		String type = face.getType();
		if(LessonFaceType.AGE.getCode().equals(type)){
			list = service.getEnumCodeAndVal(AgeGroup.values()); 
		}else if(LessonFaceType.FACIALCONTOUR.getCode().equals(type)){
			 list = service.getEnumCodeAndVal(FacialContour.values());
		}else if(LessonFaceType.SEASONCOLOR.getCode().equals(type)){
			list = service.getEnumCodeAndVal(SeasonColor.values()); 
		}else if(LessonFaceType.SKINTONE.getCode().equals(type)){
			list = service.getEnumCodeAndVal(SkinTone.values()); 
		}else if(LessonFaceType.GENDER.getCode().equals(type)){
			list = service.getEnumCodeAndVal(Gender.values()); 
		}else if(LessonFaceType.TYPE.getCode().equals(type)){
			list = service.getEnumCodeAndVal(LessonType.values()); 
		}
		
		return list;
	}
	
	@RequestMapping(value = "/save_lessonFace", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	@ResponseBody
	public LessonFace saveLessonFace(HttpServletRequest request, LessonFace  lessonFace) {

		if (lessonFace == null) {
			lessonFace = new LessonFace();
			lessonFace.setError(ErrMsg.LESSON_FACE_NO_SAVE_ERR);
			return lessonFace;
		}
		String lessonId = lessonFace.getLessonId();

		if (StringUtil.isEmpty(lessonId)) {
			lessonFace.setError(ErrMsg.LESSON_FACE_NO_SAVE_ERR);
			return lessonFace;
		}	
		
		List<LessonFace> list = service.getLessonFaceOverlapCheck(lessonFace);
		if(list.size()>0) {
			lessonFace.setError(ErrMsg.LESSON_FACE_OVERLAB_ERR);
			return lessonFace;
		}
		
		String userId = login.getUserId();

		try {
			if (lessonFace.getSeq() == 0) { // 신규 (수정이면 시퀀스가 있음)
				lessonFace = service.addLessonFace(lessonFace, userId);
			} else {
//				lessonFace = service.saveResource(file, resource, userId);
			}
		} catch (Exception e) {
			lessonFace.setError(ErrMsg.LESSON_FACE_NO_SAVE_ERR);
			return lessonFace;
		}
		
		lessonFace.setOk();
		return lessonFace;
	}
	
	@RequestMapping(value="/delete_lesson", method=RequestMethod.POST)
	public String delete(Lesson lesson){
		
		if(lesson == null) {
			lesson = new Lesson();
			lesson.setError(ErrMsg.LESSON_DELETE_ERR);
			return "error";
		}
		String lessonId = lesson.getLessonId();
		
		if(StringUtil.isEmpty(lessonId)) {
			lesson.setError(ErrMsg.LESSON_DELETE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();		
		lesson = service.deleteLesson(lesson, userId);		
		lesson.setOk();
				
		String nextPage = "redirect:/w1/lesson/go_list";
		return nextPage;
		
	}
	
	
	@ResponseBody
	@RequestMapping(value="/delete_lesson_face", method=RequestMethod.POST)
	public LessonView deleteLessonFace(
			@RequestParam(value="lessonId") String lessonId,
			@RequestParam(value="lessonFaceId") String lessonFaceId) throws Exception{
		
		LessonView lessonView = new LessonView();
		String userId = login.getUserId();		
		List<LessonFace> ctList = service.deleteLessonFace(lessonId,lessonFaceId,userId);
		
		lessonView.setLessonFaceList(ctList);
		return lessonView;
	}	
	
	

	

	
}
