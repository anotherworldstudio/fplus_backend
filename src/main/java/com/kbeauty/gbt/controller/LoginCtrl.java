package com.kbeauty.gbt.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Mileage;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.OAuthType;
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.view.PointCondition;
import com.kbeauty.gbt.oauth.KakaoApi;
import com.kbeauty.gbt.service.LoginService;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.service.OAuth;

@RequestMapping("/w1")
@Controller
@Slf4j
public class LoginCtrl {
	
	@Value("${server.url}")
	String serverUrl;
	
	@Value("${server.port}")
	String serverPort;
	
	@Value("${custom.oauth2.kakao.client-id}") 
	String kakaoClientId;
	
	@Value("${custom.oauth2.kakao.client-secret}")
	String kakaoSecret;

	@Autowired
	private LoginService service;

	@Resource
	private Login login;
	
	@GetMapping("/login")
	public String login(Model model) {		
		return "login";
	}	
	
	@GetMapping("/login_out")
	public String loginOut(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		return "login";
	}

	@PostMapping("/login_check")
	public String loginCheck(HttpServletRequest request, HttpServletResponse response, User loginUser) {
		HttpSession session = request.getSession();
		String email = loginUser.getEmail();
		String password = loginUser.getPassword();
		String ouathType = loginUser.getOauthType();
		
		if(!"1016".equals(password)) { // TODO 임시 로직 삭제 필요
			return "redirect:/error/login_fail";
		}
		
		User user = service.isUserOauth(loginUser);
		
		login.setUserId(user.getUserId());
		login.setUserName(user.getUserName());
		login.setUserRole(user.getUserRole());
				
		session.setAttribute("userId", user.getUserId());
		session.setAttribute("userName", user.getUserName());
		session.setAttribute("userRole", user.getUserRole());
		session.setAttribute("userOauthType", user.getOauthType());
		
		TokenUtils.setJwtHeader(request, user);		
		user.setOk();
		
		String nextPage = "redirect:/w1";	
		return nextPage;
	}
	
	@ApiOperation(value = "OAuth 이동", notes = "카카오,네이버,구글등의 OAuth 제공 사이트로 이동한다.")	
	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(Login login, HttpServletResponse response) throws Exception{
		String snsType = "";
		snsType = "kakao";
		final OAuth20Service service = getService(snsType);		
		final String authorizationUrl = service.getAuthorizationUrl();
		
//		response.sendRedirect(authorizationUrl);
		
		return "redirect:" + authorizationUrl;
	}

	private OAuth20Service getService(String snsType) {
		String clientId = kakaoClientId;
		String clientSecret = kakaoSecret;		
		String redirectUrl = getRedirectUrl(snsType);
		
		final OAuth20Service service = new ServiceBuilder(clientId).apiSecret(clientSecret).callback(redirectUrl).build(KakaoApi.instance());
		
		return service;
	}
	
	private String getRedirectUrl(String snsType) {
		
		if("http://localhost".equals(serverUrl)) {
			return serverUrl + ":" + serverPort + "/w1/oauth2/code/" + snsType;
		}else {			
			return serverUrl + "/w1/oauth2/code/" + snsType;
		}
		
	}	
	
	@ApiOperation(value = "Kakao Redirection", tags = "sample", notes = "카카오에서 로그인 이후 Redirection처리")
	@RequestMapping(value="/oauth2/code/kakao", method=RequestMethod.GET)
	public String goKakao( @ApiParam(value = "인증코드", required = true)  @RequestParam(value = "code",  required = false) String code,
		      				@RequestParam(value = "state", required = false) String state,
		      				HttpServletRequest request, 
		      				HttpServletResponse response, Model model) throws Exception{
		HttpSession session = request.getSession();
		
		log.debug(code);
		log.debug(state);
		
		final OAuth20Service oAuthService = getService("kakao");
		final OAuth2AccessToken accessToken = oAuthService.getAccessToken(code);	
		
		log.debug("Got the Access Token!");
		log.debug("(The raw response looks like this: " + accessToken.getRawResponse() + ")");
		
		final OAuthRequest req = new OAuthRequest(Verb.GET, KakaoApi.instance().getInfoUrl());
		oAuthService.signRequest(accessToken, req);
		String userRole = "";
		try (Response res = oAuthService.execute(req)) {
//			System.out.println("Got it! Lets see what we found...");
//			System.out.println();
//			System.out.println(res.getCode());
//			System.out.println(res.getBody());
			JSONObject result = new JSONObject(res.getBody());
			String id = String.valueOf(result.get("id"));
	        String name = getString(result.getJSONObject("properties"),"nickname");
	        String profile_image = getString(result.getJSONObject("properties"),"profile_image");
	        String thumbnail_image = getString(result.getJSONObject("properties"),"thumbnail_image");
	        String email = getString(result.getJSONObject("kakao_account"),"email");
	        
	        // email로 조회해서 해당 사용자가 관리자인지 확인한다.
	        // 관리자면 처리 / 아니면 다시 로그인 화면으로 이동
	        
	        User loginUser = new User();
	        loginUser.setEmail(email);
	        loginUser.setImageDir(thumbnail_image);
	        loginUser.setOauthType(OAuthType.KAKAO.getCode());
	        loginUser = service.isAdminUser(loginUser);
	        if(loginUser != null ) {
				if(loginUser.isAdmin()||loginUser.isProfessional()) {
					userRole = loginUser.getUserRole();
					TokenUtils.setJwtHeader(request, loginUser);
					login.setUserId(loginUser.getUserId());
					login.setUserName(loginUser.getUserName());
					login.setUserRole(loginUser.getUserRole());
					session.setAttribute("userId", loginUser.getUserId());
					session.setAttribute("userName", loginUser.getUserName());
					session.setAttribute("userRole", loginUser.getUserRole());
				}
	        }
		}
		
		// email 조회 해서 없으면, 사용자 등록 화면
		// 있으면 로그인 처리 이후에 메인 페이지로 이동
		
		String nextPage = "redirect:/w1";
		
//		nextPage = "redirect:https://gbt.beautej.com/w1";
		
		return nextPage;
			
	 }
	
//	protected JSONObject requestSns(String code, String url, SnsService service) throws Exception {
//	    OAuth2AccessToken accessToken = service.getAccessToken(code);
//	    OAuthRequest request = new OAuthRequest(Verb.GET, url, service);
//	 
//	    request.addHeader("Authorization", accessToken.getAccessToken());
//	    service.signRequest(accessToken, request);
//	 
//	    return new JSONObject(request.send().getBody());	
//	}	
	
	private String getString(JSONObject jsonObj, String key) {
		try {
			return jsonObj.getString(key);
		} catch (JSONException e) {
			return "";
		}
	}
	
//	
//	// 애플 로그인 호출
//	@RequestMapping(value = "/getAppleAuthUrl", method = RequestMethod.GET)
//	@ResponseBody
//	public String getAppleAuthUrl(HttpServletRequest request) throws Exception {
//		
//	    String reqUrl = service.getAppleUrl();
//	    return reqUrl;
//	    
//	}
//	
	
}
