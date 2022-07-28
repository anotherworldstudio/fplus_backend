package com.kbeauty.gbt.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.AppleLogin;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.OAuthType;
import com.kbeauty.gbt.entity.enums.UserStatus;
import com.kbeauty.gbt.entity.view.AppleLoginView;
import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.service.LoginService;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Login REST Controller")
@RequestMapping("/v1")
@Slf4j
public class LoginRestCtrl {

	@Autowired
	private LoginService service;
	
	@ApiOperation(value = "Login", notes = "Access Token, OAuth 사이트 구분 추가 필요")
    @ApiResponses({
    	@ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value = "/login", method = {RequestMethod.POST})
	public User login(HttpServletRequest request, HttpServletResponse response, @RequestBody User loginUser) {
		// email 전송됨
		// 1. 기 사용자 유무 확인
		// 2. Cache에 있는지? DB에 있는지?
		// 3. 없으면, header 키 생성 이후 STATUS ==> REG 전송 (키 내부에 상태 필드 추가)
		// 4. 있으면, header 키 생성 이후 STATUS ==> CON 전송
		// TokenUtils.isValidToken(request); 테스트용
		//Apple 임시 체크
		
		User user = service.isUser(loginUser);
//		User user = service.isUserOauth(loginUser);
		TokenUtils.setJwtHeader(response, user);
		user.setOk();

		return user;
	}
	
	@ApiOperation(value = "Apple Login", notes = "Access Token, OAuth 사이트 구분 추가 필요")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value = "/login_apple", method = {RequestMethod.POST})	
	public User loginApple(HttpServletRequest request,HttpServletResponse response, @RequestBody AppleLogin appleLogin) {
		
		User user = service.isUserApple(appleLogin);
		if(user == null) {
			return null;
		}
		TokenUtils.setJwtHeader(response, user);
		user.setOk();
		
		return user;
	}
	
	@RequestMapping(value = "/login_oauth", method = {RequestMethod.POST})
	public User loginOauth(HttpServletRequest request, HttpServletResponse response, @RequestBody User loginUser) {
		if (loginUser.getEmail() == null || "".equals(loginUser.getEmail()) || loginUser.getOauthType() == null	|| "".equals(loginUser.getOauthType())) {
			return null;
		}
		User user = service.isUserOauth(loginUser);
		TokenUtils.setJwtHeader(response, user);
		user.setOk();
		
		return user;
	}
	
	@RequestMapping(value = "/duplicate_user", method = RequestMethod.GET)
	public CommonView isDuplicate(@RequestParam String userName) {
		CommonView view = new CommonView();
		User user = service.isDuplicate(userName);
		if(user != null) {			
			view.setError(ErrMsg.DUPLICATE);
			return view;
		}
		
		view.setOk();
		return view;
	}

}
