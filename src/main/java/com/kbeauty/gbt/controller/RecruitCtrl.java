package com.kbeauty.gbt.controller;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Recruit;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.enums.*;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.RecruitCondition;
import com.kbeauty.gbt.entity.view.RecruitView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.RecruitService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@RequestMapping("/w1/recruit")
@Controller
@Slf4j
public class RecruitCtrl {

	@Autowired
	RecruitService service;

	@Resource
	private Login login;
	
	private final static String conditionKey = "RecruitCondition";

	private void setCondition(HttpServletRequest request, RecruitCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}

	
	private RecruitCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		RecruitCondition condiiton = (RecruitCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, RecruitCondition condition, Model model) {
		
		String reset = request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new RecruitCondition();
			condition.setRecruitType(RecruitType.EVENT.getCode());
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = new RecruitCondition();
					condition.setRecruitType(RecruitType.EVENT.getCode());
				}		
			}
		}
		
		
		model.addAttribute("condition", condition);
		
		return "admin/recruit/list";
	}
	
	@GetMapping("/go_create")
	public String goCreate() {
		return "admin/recruit/create";
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String create(Recruit recruit,
						 @RequestParam("mainUploadFile") MultipartFile mainFile,
						 @RequestParam("fileName") List<MultipartFile> files,
						 @RequestParam("resourceType") List<String> resourceType,
						 @RequestParam("resourceTitle") List<String> resourceTitle,
						 @RequestParam("resourceContent") List<String> resourceContent,
						 @RequestParam("url") List<String> url,
						 Model model
			) throws Exception{
		
		List<Resources> list = new ArrayList<>();
//		Resources res = null;
//		
//		for (MultipartFile file : files) {
//			log.debug(file.getName());
//		}
		
		String userId = login.getUserId();
		
		String recruitId = service.getRecruitId();
		long recruitSeq = service.getRecruitSeq();
		recruit.setRecruitId(recruitId);
		recruit.setSeq(recruitSeq);
		recruit.setUserId(userId);

		
		RecruitView view = new RecruitView();
		view.setRecruit(recruit);
		view.setResourceList(list);
		
		service.create(view);
		
		String nextPage = "redirect:/w1/recruit/detail/" + recruitId;
		
		return nextPage;
	}
	

	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<RecruitView> getRecruitListView(HttpServletRequest request, @RequestBody RecruitCondition condition) {
		
		PagingList<RecruitView> list = new PagingList<>();
		if(condition == null) condition = new RecruitCondition();
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
		
		List<RecruitView> contentList = service.getRecruitViewListNotAnother(condition);
		if(contentList == null || contentList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getRecruitListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(contentList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}	
	
	@RequestMapping(value="/delete_main-img")
	@ResponseBody
	public Recruit deleteManiImg(HttpServletRequest request, @RequestBody Recruit recruit) {
		
		if(recruit == null) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return recruit;
		}
		String recruitId = recruit.getRecruitId();
		
		if(StringUtil.isEmpty(recruitId)) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return recruit;
		}
		
		String userId = login.getUserId();
		
		Recruit dbcontent = service.getRecruit(recruitId);
		dbcontent.setMainImage("");
		
		recruit = service.saveRecruit(dbcontent, userId);
		recruit.setOk();

		recruit.setMainImage(RecruitService.NO_RECRUIT_IMG_PATH);
		
		return recruit;
	}	
	
	@RequestMapping(value="/detail/{recruitId}", method=RequestMethod.GET)
	public String getRecruitView(
			@PathVariable("recruitId") String recruitId,
			Model model
			) { 
		
		String userId = login.getUserId();
		
		RecruitView recruitView = service.getRecruitView(recruitId, userId);
		if(recruitView == null) {
			recruitView = new RecruitView();
			recruitView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		recruitView.setOk();
		
		model.addAttribute("recruitView", recruitView);
		
		return "admin/recruit/detail";
	}
	
	@RequestMapping(value="/save_recruit", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String save(Recruit recruit, @RequestParam("mainUploadFile") MultipartFile mainFile) throws Exception{
		
		if(recruit == null) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String recruitId = recruit.getRecruitId();
		
		if(StringUtil.isEmpty(recruitId)) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();

		Recruit dbcontent = service.getRecruit(recruitId);
		dbcontent.setRecruitType (recruit.getRecruitType ());
		dbcontent.setTitle       (recruit.getTitle       ());
		dbcontent.setContent     (recruit.getContent     ());
		dbcontent.setStartDate   (recruit.getStartDate   ());
		dbcontent.setEndDate     (recruit.getEndDate     ());
		dbcontent.setActive      (recruit.getActive      ());
		dbcontent.setStatus      (recruit.getStatus      ());

		dbcontent.setMainImage    (recruit.getMainImage    ());

		recruit = service.saveRecruit(dbcontent, userId);
		
		recruit.setOk();
		
		String nextPage = "redirect:/w1/recruit/detail/" + recruit.getRecruitId();
		return nextPage;
	}
	

	
	@RequestMapping(value="/delete_recruit", method=RequestMethod.POST)
	public String delete(Recruit recruit){
		
		if(recruit == null) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String recruitId = recruit.getRecruitId();
		
		if(StringUtil.isEmpty(recruitId)) {
			recruit.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();
		recruit = service.deleteRecruit(recruit, userId);
		recruit.setOk();
				
		String nextPage = "redirect:/w1/recruit/go_list";
		return nextPage;
	}


	
}
