package com.kbeauty.gbt.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinDoctor;
import com.kbeauty.gbt.entity.domain.SkinGroup;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.UserSkinStatus;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.entity.view.DoctorSkinView;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.UserSkinListView;
import com.kbeauty.gbt.entity.view.UserSkinView;
import com.kbeauty.gbt.service.SkinService;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/skin")
@Controller
@Slf4j
public class SkinCtrl {

	@Autowired
	SkinService service;

	@Resource
	private Login login;

	private final static String conditionKey = "UserSkinCondition";
	
	private void setCondition(HttpServletRequest request, UserSkinCondition condition) {
		// TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}

	private UserSkinCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		UserSkinCondition condiiton = (UserSkinCondition) session.getAttribute(conditionKey);
		return condiiton;
	}

	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, UserSkinCondition condition, Model model) {

		String reset = (String) request.getParameter("reset");
		if (YesNo.isYes(reset)) {
			condition = new UserSkinCondition();
		} else {
			if (condition == null || condition.isEmpty()) {
				condition = getCondition(request);
				if (condition == null) {
					condition = new UserSkinCondition();
				}
			}
		}

		model.addAttribute("condition", condition);

		return "admin/skin/list";
	}

	@GetMapping("/go_doctor_list")
	public String goDoctorList(HttpServletRequest request, UserSkinCondition condition, Model model) {

		String reset = (String) request.getParameter("reset");
		if (YesNo.isYes(reset)) {
			condition = new UserSkinCondition();
		} else {
			if (condition == null || condition.isEmpty()) {
				condition = getCondition(request);
				if (condition == null) {
					condition = new UserSkinCondition();
				}
			}
		}

		model.addAttribute("condition", condition);

		return "admin/skin/doctor_list";
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public PagingList<UserSkinListView> getUserSkinListView(HttpServletRequest request,
			@RequestBody UserSkinCondition condition) {

		PagingList<UserSkinListView> list = new PagingList<>();
		if (condition == null)
			condition = new UserSkinCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);

		int currPage = condition.getPage();
		if (currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);

		List<UserSkinListView> userSkinList = service.getUserSkinList(condition);
		if (userSkinList == null || userSkinList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);
			return list;
		}

		int totalCnt = service.getUserSkinListCnt(condition);
		paging.setTotalCount(totalCnt);

		list.setList(userSkinList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}

	@RequestMapping(value = "/doctor_list")
	@ResponseBody
	public PagingList<UserSkinListView> getREQSkinListView(HttpServletRequest request,
			@RequestBody UserSkinCondition condition) {
		String doctorId; 
		doctorId = login.getUserId(); //현재 로그인 돼있는 아이디
		PagingList<UserSkinListView> list = new PagingList<>();
		if (condition == null)
			condition = new UserSkinCondition();
		condition.setDoctorId(doctorId); //전달받은
		Paging paging = new Paging();
		paging.setCondition(condition);

		int currPage = condition.getPage();
		if (currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);

		List<UserSkinListView> userSkinList = service.getREQSkinList(condition); 
		if (userSkinList == null || userSkinList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);
			return list;
		}

		int totalCnt = service.getREQSkinListCnt(condition);
		paging.setTotalCount(totalCnt);

		list.setList(userSkinList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}

	@RequestMapping(value = "/detail/{skinid}", method = RequestMethod.GET)
	public String getContentView(@PathVariable("skinid") String skinId, Model model) {

		String userId = login.getUserId();

		UserSkinView userSkinView = service.getUserSkinView(skinId, userId);
		if (userSkinView == null) {
			userSkinView = new UserSkinView();
			userSkinView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		boolean notCheck = userSkinView.getSkin().getStatus().equals(UserSkinStatus.REG.getCode());
		userSkinView.setOk();

		model.addAttribute("check", notCheck);
		model.addAttribute("userSkinView", userSkinView);

		return "admin/skin/detail";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String delete(Skin skin) {

		if (skin == null) {
			skin.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String skinId = skin.getSkinId();

		if (StringUtil.isEmpty(skinId)) { 	
			skin.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}

		String userId = login.getUserId();
		service.deleteUserSkin(skinId, userId);
		skin.setOk();

		String nextPage = "redirect:/w1/skin/go_list";
		return nextPage;
	}

	@RequestMapping(value = "/doctor_detail/{skinid}", method = RequestMethod.GET)
	public String getContentView2(@PathVariable("skinid") String skinId, Model model) {
		String userId = login.getUserId();
		SkinDoctor skin_user = new SkinDoctor();
		log.debug("skinID추출: " + skinId);
		skin_user.setSkinId(skinId);
		skin_user.setDoctorId(userId);

		boolean isDoctor = service.doctorOrAdmin(userId); // Admin계정인지 Doctor계정인지 확인 Doctor일시 true 리턴
		if (!isDoctor) { //Admin일시
			SkinDoctor adminScore = service.getAVGScore(skinId); // 해당 skinId에 대한 Doctor점수들의 평균값 리턴
			if (adminScore == null) {
				adminScore = new SkinDoctor();
			}
			model.addAttribute("doctorScore", adminScore); 
		} else {
			// skinId랑 userId 로 구성돼있는 SKINDOCTOR DB정보가 있는지 확인
			SkinDoctor mySkinDoctor = service.getMySkinDoctor(skin_user); // 해당 skinId에 본인이 평가한 점수데이터 리턴
			if (mySkinDoctor == null) { // 없을때 에러나서 잠깐만 해놓음
				mySkinDoctor = new SkinDoctor();
			}
			model.addAttribute("doctorScore", mySkinDoctor);
		}

		UserSkinView userSkinView = service.getUserSkinView(skinId, userId);

		if (userSkinView == null) {
			userSkinView = new UserSkinView();
			userSkinView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		userSkinView.setOk();
		model.addAttribute("isDoctor", isDoctor);
		model.addAttribute("userSkinView", userSkinView);

		return "admin/skin/doctor_detail";
	}

	@GetMapping("/go_skingroup_list")
	public String goSkinGroupList(HttpServletRequest request, UserSkinCondition condition, Model model) {

		String reset = (String) request.getParameter("reset");
		if (YesNo.isYes(reset)) {
			condition = new UserSkinCondition();
		} else {
			if (condition == null || condition.isEmpty()) {
				condition = getCondition(request);
				if (condition == null) {
					condition = new UserSkinCondition();
				}
			}
		}

		model.addAttribute("condition", condition);

		return "admin/skin/skingroup_list";
	}

	@RequestMapping(value = "/skingroup_list")
	@ResponseBody
	public PagingList<SkinGroup> getSkinGroupList(HttpServletRequest request,
			@RequestBody UserSkinCondition condition) {

		PagingList<SkinGroup> list = new PagingList<>();
		if (condition == null)
			condition = new UserSkinCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);

		int currPage = condition.getPage();
		if (currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);

		List<SkinGroup> userSkinList = service.getSkinGroupList(condition);
		if (userSkinList == null || userSkinList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);
			return list;
		}

		int totalCnt = service.getSkinGroupListCnt(condition);
		paging.setTotalCount(totalCnt);

		list.setList(userSkinList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}

	@RequestMapping(value = "/update_status_wait")
	@ResponseBody
	public CommonView getSkinGrwoupList(HttpServletRequest request, @RequestBody String skinId) {

		CommonView view = new CommonView();
		try {
			service.updateStatusWait(skinId);
		} catch (Exception e) {
			view.setError(ErrMsg.SKIN_NO_ID);
			return view;
		}
		view.setOk();
		return view;

	}

	@RequestMapping(value = "/doctor_save", method = RequestMethod.POST)
	public String saveDoctorSkin(HttpServletRequest request, Model model, SkinDoctor skin,
			@RequestParam("doctor_seq") String doctorSeq) {

		String userId = login.getUserId();
		skin.setDoctorId(userId);
		skin.setSeq(Integer.parseInt(doctorSeq));
		Skin outSkin = service.saveSkinDoctor(skin);
		UserSkinView userSkinView = service.getUserSkinView(skin.getSkinId(), userId);

		if (userSkinView == null) {
			userSkinView = new UserSkinView();
			userSkinView.setError(ErrMsg.NO_RESULT);
			return "";
		}

		if (outSkin == null) {
			userSkinView.setError(ErrMsg.SKIN_NOT_SAVE);
		}

		userSkinView.setOk();

		model.addAttribute("userSkinView", userSkinView);
		
		return getContentView2(skin.getSkinId(), model);
	}

}
