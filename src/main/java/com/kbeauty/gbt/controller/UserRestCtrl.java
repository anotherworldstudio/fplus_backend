package com.kbeauty.gbt.controller;

import java.io.IOException;
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
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.domain.UserSkin;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FollowActionType;
import com.kbeauty.gbt.entity.enums.FollowStatus;
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.enums.UserStatus;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.UserCondition;
import com.kbeauty.gbt.entity.view.UserFaceView;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.entity.view.UserView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.util.StringUtil;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "User Controller")
@RequestMapping("/v1/user")
public class UserRestCtrl {
	
	@Autowired
	private UserService service;
		
	@ApiOperation(value = "save", notes = "사용자 저장 함수.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!"),
            @ApiResponse(code = 500, message = "Internal Server Error !!"),
            @ApiResponse(code = 404, message = "Not Found !!")
    })
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public User save(@RequestBody User user) {
		user = service.save(user);
		user.setOk();
		return user;
	}
	
	@ApiOperation(value = "saveBasic", notes = "기본 저장 함수.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!"),
            @ApiResponse(code = 500, message = "Internal Server Error !!"),
            @ApiResponse(code = 404, message = "Not Found !!")
    })
	@RequestMapping(value="/save_basic/{userId}", method=RequestMethod.POST)
	public User saveBasic(
			@ApiParam(value = "사용자ID", required = false, example = "사용자GUID") 
			@PathVariable String userId, 
			@ApiParam(value = "사용자기본정보", required = true, example="email,username,agreeyn,privateyn,ageoveryn")
			@RequestBody User inputUser
			) {
		User user = service.getUser(userId);
		if(user == null) {
			user = new User();
			user.setError(ErrMsg.NO_USER);
			return user;
		}
		user.setEmail(inputUser.getEmail());
		user.setUserName(inputUser.getUserName());
		user.setAgreeYn(inputUser.getAgreeYn());
		user.setPrivateYn(inputUser.getPrivateYn());
		user.setAgeoverYn(inputUser.getAgeoverYn());

//		TODO oauthtype 처리로직 확인
 
		user = service.save(user);
		user.setOk();
		return user;
	}

	@ApiOperation(value = "savePersonal", notes = "개인 정보 저장 함수.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/save_personal/{userId}", method=RequestMethod.POST)
	public User savePersonal(
			@ApiParam(value = "사용자ID", required = false, example = "사용자GUID") 
			@PathVariable String userId, 
			@ApiParam(value = "사용자기본정보", required = true)
			@RequestBody User inputUser
			) {
		User user = service.getUser(userId);
		if(user == null) {			
			user = service.getUser(inputUser.getUserId());
			if(user == null) {
				user = new User();
				user.setError(ErrMsg.NO_USER);
				return user;
			}
		}
		
		user.setSex(inputUser.getSex());
		user.setCountry(inputUser.getCountry());
		user.setBirthDay(inputUser.getBirthDay());
		user.setUserRole(UserRole.USER.getCode());
		user.setStatus(UserStatus.CON.getCode());
		user = service.save(user);
		user.setOk();
		return user;
	}
	
	@ApiOperation(value = "saveOther", notes = "자기소개,웹사이트 수정")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/save_others/{userId}", method=RequestMethod.POST)
	public User saveOther(
			@ApiParam(value = "사용자ID", required = false, example = "사용자GUID") 
			@PathVariable String userId, 
			@ApiParam(value = "사용자기본정보", required = true)
			@RequestBody User inputUser
			) {
		User user = service.getUser(userId);
		if(user == null) {			
			user = service.getUser(inputUser.getUserId());
			if(user == null) {
				user = new User();
				user.setError(ErrMsg.NO_USER);
				return user;
			}
		}
		user.setComment(inputUser.getComment());
		user.setUserName(inputUser.getUserName());
		user.setWebSite(inputUser.getWebSite());
		
		user = service.save(user);
		user.setOk();
		return user;
	}
	
	@ApiOperation(value = "saveSkin", notes = "피부 정보 저장 함수.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/save_skin/{userId}", method=RequestMethod.POST)
	public UserSkin saveSkin(
			@ApiParam(value = "사용자ID",  required = false, example = "사용자GUID") 
			@PathVariable String userId, 
			@ApiParam(value = "사용자피부", required = true )
			@RequestBody UserSkin inputUserSkin
			) {
		User user = service.getUser(userId);
		if(user == null) {			
			user = service.getUser(inputUserSkin.getUserId());
			if(user == null) {
				inputUserSkin = new UserSkin();
				inputUserSkin.setError(ErrMsg.NO_USER);
				return inputUserSkin;
			}
		}
		
		service.saveUserSkin(inputUserSkin);
		inputUserSkin.setOk();
		return inputUserSkin;
	}
	
	@ApiOperation(value = "saveImg", notes = "사용자 이미지 저장 함수.")	
	@RequestMapping(value="/save_img", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")  
	public User saveImg(@RequestPart MultipartFile faceImg, @RequestParam String userId, @RequestParam String fileName,
			HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception{
		
		//사용자 아이디
		User user = service.getUser(userId);
		if(user == null) {			
			user = new User();
			user.setError(ErrMsg.NO_USER);
			return user;
		}
		
		try {
			user = service.saveImg(faceImg, user, fileName);
		} catch (IOException e) {
			user = new User();
			user.setError(ErrMsg.USER_IMG_ERR);
			return user;
		}
		
		user.setOk();
		return user;
	}
	
	@ApiOperation(value = "deleteImg", notes = "사용자 이미지 삭제 함수.")
	@RequestMapping(value="/delete_img/{userId}", method=RequestMethod.POST)
	public User deleteImg(
			   @ApiParam(value = "사용자ID",  required = false, example = "사용자GUID") 
			   @PathVariable String userId) {
		
		//사용자 아이디
		User user = service.getUser(userId);
		if(user == null) {			
			user = new User();
			user.setError(ErrMsg.NO_USER);
			return user;
		}
		
		user = service.deleteImg(user);
		user.setOk();
		
		return user;
	}
	
	@ApiOperation(value = "deleteUser", notes = "사용자 삭제 함수 Flag")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/delete/{userId}", method=RequestMethod.POST)
	public User deleteUser(
			@ApiParam(value = "사용자ID",  required = false, example = "사용자GUID") 
			@PathVariable("userId") String userId
			) {	
		User user = service.getUser(userId);
		if(user == null) {
			user = new User();
			user.setError(ErrMsg.NO_USER);
			return user;
		}
		
		service.deleteUser(user);
		user.setOk();
		return user;
	}
	
	@ApiOperation(value = "getUserView", notes = "사용자 조회함수")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/{userId}", method=RequestMethod.GET)
	public UserView getUserView(HttpServletRequest request,
			@ApiParam(value = "사용자ID", required = false, example = "사용자GUID")
			@PathVariable("userId") String userId
			) { 
		
		String loginUserId = TokenUtils.getTokenUserId(request);
		
		UserView userView = service.getUserView(userId, loginUserId);
		if(userView == null) {
			userView = new UserView();
			userView.setError(ErrMsg.NO_USER);
			return userView;
		}
		userView.setOk();
		return userView;
	}
	
	@ApiOperation(value = "getUserListView", notes = "사용자 목록 조회함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/list", method=RequestMethod.POST)
	public PagingList<UserListView> getUserListView(
			@ApiParam(value = "조회 조건", required = true )
			@RequestBody UserCondition condition
			) {
		
		PagingList<UserListView> list = new PagingList<>();
		if(condition == null) condition = new UserCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
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
	
	@ApiOperation(value = "searchUserName", notes = "사용자명 검색")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/search", method=RequestMethod.POST)
	public PagingList<UserListView> searchUserName(
			@ApiParam(value = "사용자명", required = true )
			@RequestBody UserCondition condition
			) {
	
		PagingList<UserListView> list = new PagingList<>();
		if(condition == null) condition = new UserCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		List<UserListView> userList = service.searchUserName(condition);
		if(userList == null || userList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		paging.setTotalCount(userList.size());		
		list.setList(userList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}

	
	//gbt

	@ApiOperation(value = "getUserFaceList", notes = "유저 얼굴정보 저장리스트 조회")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/get_user_face_list", method=RequestMethod.POST)
	public UserFaceView getUserFaceList(
			@ApiParam(value = "유저ID", required = true )
			@RequestBody UserCondition condition
			) {
		
		UserFaceView view = new UserFaceView();
		List<UserFace> list = new ArrayList<>();
		try {
			list = service.getUserFaceList(condition);
			view.setList(list);
			view.setCount(list.size());
			view.setOk();
		} catch (Exception e) {			
			view = new UserFaceView();
			view.setError(ErrMsg.USER_FACE_VIEW_ERR);
		}
		
		return view;
	}
	
	@ApiOperation(value = "saveMyFace", notes = "자기 자신 얼굴정보 저장,수정")
	@RequestMapping(value="/save_my_face", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	@ResponseBody
	public UserFaceView saveMyFace(@RequestPart MultipartFile faceImg, @RequestParam String userId, @RequestParam String fileName,
			@RequestParam String skinTone,
			@RequestParam String seasonColor,
			@RequestParam String facialContour,
			@RequestParam String age,
			@RequestParam String gender,
			HttpServletRequest request, RedirectAttributes redirectAttributes, HttpServletResponse response) throws Exception{
		UserFace userFace  = new UserFace();
		userFace.setSkinTone(skinTone);
		userFace.setAge(age);
		userFace.setFacialContour(facialContour);
		userFace.setGender(gender);
		userFace.setSeasonColor(seasonColor);
		String loginUserId = TokenUtils.getTokenUserId(request);
		UserFaceView view = new UserFaceView();
		try {
			userFace = service.saveMyUserFace(faceImg,fileName,userFace,loginUserId);
			view.setUserFace(userFace);
			view.setOk();
		} catch (Exception e) {			
			view = new UserFaceView();
			view.setError(ErrMsg.USER_FACE_MY_ERR);
		}
		
		return view;
	}

	@ApiOperation(value = "setUserRole", notes = "유저 타입 변경 ex) 개발자, 전문가, 일반회원")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/set_user_role", method=RequestMethod.POST)
	public User getUserFaceList(
			@ApiParam(value = "", required = true )
			@RequestBody User user
			) {
		try {
			user = service.setUserRole(user);
			user.setOk();
		} catch (Exception e) {			
			user = new User();
			user.setError(ErrMsg.USERROLE_SET_ERR);
		}
		
		return user;
	}
		
	
	
	
	
}
