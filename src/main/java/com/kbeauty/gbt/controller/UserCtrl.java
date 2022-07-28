package com.kbeauty.gbt.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ConstantMapKey;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ModelCondition;
import com.kbeauty.gbt.entity.view.ModelUserList;
import com.kbeauty.gbt.entity.view.UserCondition;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.entity.view.UserView;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.util.StringUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/user")
@Controller
@Slf4j
public class UserCtrl {

	@Autowired
	UserService service;

	@Resource
	private Login login;
	
	private final static String conditionKey = "UserCondition";
	private final static String modelConditionKey = "ModelCondition";
	
	private void setCondition(HttpServletRequest request, ModelCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(modelConditionKey, condition);
	}
	
	private void setCondition(HttpServletRequest request, UserCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}
	
	private UserCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserCondition condiiton = (UserCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, UserCondition condition, Model model) {		
		
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new UserCondition();
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = new UserCondition();
//					condition.setContentType(ContentType.FEED.getCode());
				}		
			}
		}
		
		
		model.addAttribute("condition", condition);
		
		return "admin/user/list";
	}
	
	@GetMapping("/go_model_list")
	public String goModelList(HttpServletRequest request, UserCondition condition, Model model) {		
		
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new UserCondition();
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = new UserCondition();
				}		
			}
		}
		
		
		model.addAttribute("condition", condition);
		
		return "admin/user/model_list";
	}
	
	@GetMapping("/go_create")
	public String goCreate(Model model ) {
		UserView userView = new UserView();
		//기본값 설정 
		User user = new User();
		user.setCountry("KR");
		user.setSex("F");
		user.setMarketingYn("Y");
		userView.setUser(user);
		
		
		model.addAttribute("userView", userView);
		
//		return "admin/user/create";
		return "admin/user/detail";
	}	
	
	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<UserListView> getUserListView(HttpServletRequest request, @RequestBody UserCondition condition) {
		PagingList<UserListView> list = new PagingList<>();
		if(condition == null) condition = new UserCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		setCondition(request, condition);
		
		List<UserListView> userList = service.getUserList(condition);
		if(userList == null || userList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		int totalCnt = service.getUserListCnt(condition);
		paging.setTotalCount(totalCnt);		
		
		list.setList(userList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}
	
	@RequestMapping(value="/model_list")
	@ResponseBody
	public PagingList<ModelUserList> getModelListView(HttpServletRequest request, @RequestBody ModelCondition condition) {
		PagingList<ModelUserList> list = new PagingList<>();
		if(condition == null) condition = new ModelCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		setCondition(request, condition);
		
		List<ModelUserList> userList = service.getModelEventList(condition);
		if(userList == null || userList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		int totalCnt = service.getModelEventListCnt(condition);
		paging.setTotalCount(totalCnt);		
		
		list.setList(userList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}

	
	@RequestMapping(value="/create", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String create(User user, Model model ) throws Exception{
		
		String userId = login.getUserId();
		
		User inputUser = service.save(user);
		
		String nextPage = "redirect:/w1/user/detail/" + userId;
		
		return nextPage;
	}
	
	
	@RequestMapping(value="/detail/{userId}", method=RequestMethod.GET)
	public String getContentView(			
			@PathVariable("userId") String userId,
			Model model
			) { 
		String loginUserId = login.getUserId();
		
		UserView userView = service.getUserView(userId, loginUserId);
		if(userView == null) {
			userView = new UserView();
			userView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		userView.setOk();
		
		model.addAttribute("userView", userView);
		
		return "admin/user/detail";
	}
	
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public String save(HttpServletRequest request, UserView view, RedirectAttributes redirectAttributes){
		User user = view.getUser();
		
		if(user == null) {			
			user.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			redirectAttributes.addFlashAttribute(ConstantMapKey.ERROR_MSG_KEY.toString(), ErrMsg.CONTENT_NO_SAVE_ERR);			
			return "/error/validate";
		}
		
		String loginUserId = login.getUserId();
		
		if( ! StringUtil.isEmpty(user.getUserId())) {
			// 기존 사용자 조회 이후에 해당 항목만 수정함
			// 이멜/사용자명/생년월일/성별/휴대폰/국적//사용자구분//로그인구분/마켓팅/상태/비고			
			User oldUser = service.getUser(user.getUserId());
			oldUser.setEmail(user.getEmail());
			oldUser.setUserName(user.getUserName());
			oldUser.setBirthDay(user.getBirthDay());
			oldUser.setSex(user.getSex());
			oldUser.setCellphone(user.getCellphone());
			oldUser.setCountry(user.getCountry());
			oldUser.setUserRole(user.getUserRole());
			oldUser.setOauthType(user.getOauthType());
			oldUser.setMarketingYn(user.getMarketingYn());
			oldUser.setStatus(user.getStatus());
			oldUser.setComment(user.getComment());
			oldUser.setWebSite(user.getWebSite());
			user = service.save(oldUser);
		}else {
			user = service.save(user);
		}
		
		user.setOk(); 
		
		String nextPage = "redirect:/w1/user/detail/" + user.getUserId();
		return nextPage;
	}
	
	
	/**
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public String delete(Content content){
		
		if(content == null) {			
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();		
		content = service.deleteContent(content, userId);		
		content.setOk();
				
		String nextPage = "redirect:/w1/user/go_list";
		return nextPage;
	}
**/
	
}
