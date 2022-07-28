package com.kbeauty.gbt.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
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

import com.google.common.collect.ContiguousSet;
import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.PointType;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.MileExp;
import com.kbeauty.gbt.entity.view.PointCondition;
import com.kbeauty.gbt.entity.view.PointView;
import com.kbeauty.gbt.entity.view.WeatherCondition;
import com.kbeauty.gbt.entity.view.WeatherDetailView;
import com.kbeauty.gbt.service.PointService;
import com.kbeauty.gbt.service.WeatherService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/point")
@Controller
@Slf4j
public class PointCrtl {
	@Autowired
	PointService service;

	@Resource
	private Login login;

	private final static String conditionKey = "PointCondition";
	private final static String CONTENT_VIEW = "contentView";
	
	private void setCondition(HttpServletRequest request, PointCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}
	
	private PointCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		PointCondition condiiton = (PointCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	private PointCondition getDefaultContentCondition() {
		PointCondition condition = new PointCondition();
		
		return condition;
	}
	
	private void goListCommon(HttpServletRequest request, PointCondition condition, Model model) {
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

	
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, PointCondition condition, Model model) {		
		
		goListCommon(request, condition, model);
		
		return "admin/point/list";
	}
	
	@GetMapping("/go_mile_list")
	public String goMileList(HttpServletRequest request, PointCondition condition, Model model) {		
		
		goListCommon(request, condition, model);
		
		return "admin/point/mile_list";
	}
	
	@GetMapping("/go_exp_list")
	public String goExpList(HttpServletRequest request, PointCondition condition, Model model) {		
		
		goListCommon(request, condition, model);
		
		return "admin/point/exp_list";
	}
	
	
	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<PointType> getPointTypeList(HttpServletRequest request, @RequestBody PointCondition condition) {
		
		PagingList<PointType> list = new PagingList<>();
		if(condition == null) condition = new PointCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int perPageNum = condition.getPerPageNum();
		if(perPageNum == 0) {
			perPageNum = 10;
		}
		paging.setDisplayPageNum(perPageNum);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);
		
		List<PointType> pointTypeList = service.getPointTypeList(condition);
		if(pointTypeList == null || pointTypeList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		int totalCnt = service.getPointTypeListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(pointTypeList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
	}	
	
	@RequestMapping(value="/detail/{pointTypeId}", method=RequestMethod.GET)
	public String getContentView(HttpServletRequest request, PointCondition condition,
			@PathVariable("pointTypeId") String pointTypeId,
			Model model
			) { 
		goListCommon(request, condition, model);
		String userId = login.getUserId();
		PointView pointView = null;
		PointType pointType = service.findByPointTypeId(pointTypeId);
		if(pointType!=null) {
		pointView = new PointView();
		pointView.setPointType(pointType);
		//마일리스트
		
		//경험치리스트
		}
		if(pointType == null) {
			pointView = new PointView();
			pointView.setError(ErrMsg.NO_RESULT);
			model.addAttribute("pointView", pointView);
			return "";
		}
		pointView.setOk();
		
		model.addAttribute("pointView", pointView);
		return "admin/point/detail"; 
		
	}
	

	@RequestMapping(value="/mile_list")
	@ResponseBody
	public PagingList<Mileage> getMileageListView(HttpServletRequest request, @RequestBody PointCondition condition) {
		
		PagingList<Mileage> list = new PagingList<>();
		if(condition == null) condition = new PointCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int perPageNum = condition.getPerPageNum();
		if(perPageNum == 0) {
			perPageNum = 10;
		}	
		paging.setDisplayPageNum(perPageNum);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);

		SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
		if(condition.getSearchStartDate()!=null) 
		condition.setStartDate(transFormat.format(condition.getSearchStartDate()));
		if(condition.getSearchEndDate()!=null) 
		condition.setEndDate(transFormat.format(condition.getSearchEndDate()));

		List<Mileage> mileList = service.getMileageList(condition);
		
		if(mileList == null || mileList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getMileageListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(mileList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
	}	
	
	@GetMapping("/go_mile_list_popup")
	public String goMileListPopup(HttpServletRequest request, PointCondition condition, Model model,@RequestParam(required = false)String userId) {		
		goListCommon(request, condition, model);
		condition  =  (PointCondition) model.getAttribute("condition");
		if(userId!=null&&!"".equals(userId)) {
			condition.setUserId(userId);
			model.addAttribute("condition", condition);
		}
		return "admin/user/mileage_popup";
	}
	

	@RequestMapping(value="/exp_list")
	@ResponseBody
	public PagingList<Exp> getExpListView(HttpServletRequest request, @RequestBody PointCondition condition) {
		
		PagingList<Exp> list = new PagingList<>();
		if(condition == null) condition = new PointCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int perPageNum = condition.getPerPageNum();
		if(perPageNum == 0) {
			perPageNum = 10;
		}	
		paging.setDisplayPageNum(perPageNum);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);

		SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
		if(condition.getSearchStartDate()!=null) 
		condition.setStartDate(transFormat.format(condition.getSearchStartDate()));
		if(condition.getSearchEndDate()!=null) 
		condition.setEndDate(transFormat.format(condition.getSearchEndDate()));

		List<Exp> expList = service.getExpList(condition);
		
		if(expList == null || expList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getExpListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(expList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
	}	
	
	@GetMapping("/go_exp_list_popup")
	public String goExpListPopup(HttpServletRequest request, PointCondition condition, Model model,@RequestParam(required = false)String userId) {		
		goListCommon(request, condition, model);
		condition  =  (PointCondition) model.getAttribute("condition");
		if(userId!=null&&!"".equals(userId)) {
			condition.setUserId(userId);
			model.addAttribute("condition", condition);
		}
		return "admin/user/exp_popup";
	}
	
	@RequestMapping(value="/save_point_type", method=RequestMethod.POST)
	public String savePointType(HttpServletRequest request, PointView pointType) {
		PointType pointT = pointType.getPointType();
		if(pointT == null) {			
			pointT.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String pointId = pointT.getPointTypeId();
		if(StringUtil.isEmpty(pointId)) {
			pointT.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String userId = login.getUserId();
		
		//기존 content를 가지고 온다.
		PointType oldPointType = service.findByPointTypeId(pointId);
		
		oldPointType.setMileage(pointT.getMileage());
		oldPointType.setExp(pointT.getExp());
		service.save(oldPointType);
		
		String nextPage = "redirect:/w1/point/detail/" + pointId;
		return nextPage;
	}

	@RequestMapping(value = "/mile_delete", method = RequestMethod.POST)
	@ResponseBody
	public PagingList<Mileage> mileDelete(HttpServletRequest request,@RequestBody  PointCondition condition) {

		String mileId = condition.getDeleteId(); 
		if (StringUtil.isEmpty(mileId)) {
			return null;
		}

		String userId = login.getUserId();
		service.deleteMileage(mileId, userId);
		
		return getMileageListView(request, condition);
	}
	
	@RequestMapping(value = "/exp_delete", method = RequestMethod.POST)
	@ResponseBody
	public PagingList<Exp> expDelete(HttpServletRequest request,@RequestBody  PointCondition condition) {

		String expId = condition.getDeleteId(); 
		if (StringUtil.isEmpty(expId)) { 	
			return null;
		}

		String userId = login.getUserId();
		service.deleteExp(expId, userId);

		return getExpListView(request, condition);
		
	}
	
	

}
