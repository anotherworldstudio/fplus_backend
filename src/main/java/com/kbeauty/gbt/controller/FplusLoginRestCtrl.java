package com.kbeauty.gbt.controller;

import com.github.scribejava.core.oauth.OAuth20Service;
import com.kbeauty.gbt.entity.domain.AppleLogin;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.service.LoginService;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.service.UserService2;
import com.kbeauty.gbt.util.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
//@Api(value = "Fplus Login REST Controller")
@RequestMapping("/w1")
@Slf4j
public class FplusLoginRestCtrl {

	@Resource
	Login login;

	//	회원가입 로직
	@Autowired
	private UserService2 service;

	@Autowired
	private LoginService defaultService;

	@ApiOperation(value = "save", notes = "사용자 저장 함수.")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK !!"),
			@ApiResponse(code = 500, message = "Internal Server Error !!"),
			@ApiResponse(code = 404, message = "Not Found !!")
	})
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public User2 save(@RequestBody User2 user) {
		user = service.save(user);
		user.setOk();
		return user;
	}

//	아이디 중복 체크 API, 중복이면 TRUE, 아니면 FALSE 뱉어냄
//	JPA 함수
	@GetMapping("/emailCheck/{email}/exists")
	public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
		return ResponseEntity.ok(service.checkEmail(email));
	}


	@GetMapping("/login/fplus")
	public String login(Model model) {
		return "login";
	}

	@GetMapping("/login_out/fplus")
	public String loginOut(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		return "login";
	}

	@PostMapping("/login_check/fplus")
	public String loginCheck(HttpServletRequest request, HttpServletResponse response, User loginUser) {
		HttpSession session = request.getSession();
		String email = loginUser.getEmail();
		String password = loginUser.getPassword();
		String ouathType = loginUser.getOauthType();

		if(!"1016".equals(password)) { // TODO 임시 로직 삭제 필요
			return "redirect:/error/login_fail";
		}

		User user = defaultService.isUserOauth(loginUser);

		login.setUserId(user.getUserId());
		login.setUserName(user.getUserName());
		login.setUserRole(user.getUserRole());

		session.setAttribute("userId", user.getUserId());
		session.setAttribute("userName", user.getUserName());
		session.setAttribute("userRole", user.getUserRole());
		session.setAttribute("userOauthType", user.getOauthType());

		TokenUtils.setJwtHeader(request, user);
		user.setOk();

		String nextPage = "redirect:/main";
		return nextPage;
	}


}


