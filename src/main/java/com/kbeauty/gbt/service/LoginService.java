package com.kbeauty.gbt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kbeauty.gbt.controller.SkinRestCtrl;
import com.kbeauty.gbt.entity.domain.AppleLogin;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.AppleLoginStatus;
import com.kbeauty.gbt.entity.enums.CacheKey;
import com.kbeauty.gbt.entity.enums.OAuthType;
import com.kbeauty.gbt.entity.enums.UserStatus;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
public class LoginService {

	@Autowired
	UserService userService;

	@Autowired
	private CacheService cacheService;

	private User isCacheUser(User loginUser) {
		String userid = getUserId(loginUser.getEmail());
		if (userid == null)
			return null;
		User user = userService.getUser(userid);
		if (user == null) {
			// 캐쉬 내용 삭제 ==> 오류 케쉬
			cacheService.del(CacheKey.USER_EMAIL, loginUser.getEmail());
			return null;
		}
		return user;
	}

	private User isDbUser(User loginUser) {
		String email = loginUser.getEmail();
		User user = userService.getUserByEmail(email);
		return user;
	}

	private User isDbUserOauth(User loginUser) {
		String email = loginUser.getEmail();
		String oauthType = loginUser.getOauthType();
		User user = userService.getUserByEmailAndOauthType(email, oauthType);
		return user;
	}

	private User isDbUserAppleKey(User loginUser) {
		User user = userService.findByUserAppleKey(loginUser);
		return user;
	}
	
	private AppleLogin isDbAppleLogin(AppleLogin appleLogin) {
		AppleLogin returnAppleLogin = userService.findAppleLogin(appleLogin);
		return returnAppleLogin;
	}

	public User isUser(User loginUser) {

		User user = isCacheUser(loginUser);
		if (user == null) {
			user = isDbUser(loginUser);
		}

		if (user == null) { // 신규 유저 등록
			user = new User();
			user.setEmail(loginUser.getEmail());
			user.setStatus(UserStatus.REG.getCode());
			user = userService.save(user);
		}

		return user;
	}

	public User isUserOauth(User loginUser) {

		User user = isCacheUser(loginUser);
		if (user == null) {
			user = isDbUserOauth(loginUser);
		}

		if (user == null) { // 신규 유저 등록
			user = new User();
			user.setEmail(loginUser.getEmail());
			user.setOauthType(loginUser.getOauthType());
			user.setStatus(UserStatus.REG.getCode());
			user = userService.save(user);
		}

		return user;
	}

	public User isUserAppleKey(User loginUser) {

		User user = isCacheUser(loginUser);
		if (user == null) {
			user = isDbUserAppleKey(loginUser);
		}

		if (user == null) { // 신규 유저 등록
			user = new User();
			if (loginUser.getEmail() != null && !"".equals(loginUser.getEmail())) {
				user = isDbUser(loginUser);
				if(user!=null) {
					user.setUserAppleKey(loginUser.getUserAppleKey());
					user.setOauthType(OAuthType.APPLE.getCode());
					user = userService.save(user);
					return user;
				}
				user = new User();
				user.setEmail(loginUser.getEmail());
			}
			user.setUserAppleKey(loginUser.getUserAppleKey());
			user.setOauthType(OAuthType.APPLE.getCode());
			user.setStatus(UserStatus.REG.getCode());
			user = userService.save(user);
		}

		return user;
	}

	public User isUserApple(AppleLogin appleLogin) {
		// 애플로그인시도
		// 애플로그인 정보 db에저장돼있나 검사
		// 저장안돼있으면 일단저장
		//
		// 유저랑 연결안돼있으니 status 0000 으로보내기
		User user;
		AppleLogin dbAppleLogin = isDbAppleLogin(appleLogin);
		if (dbAppleLogin == null) {
			
			if (appleLogin.getEmail() != null && !"".equals(appleLogin.getEmail())) {
				User mailUser = new User();
				mailUser.setEmail(appleLogin.getEmail());
				user = isDbUser(mailUser);
				if (user != null) {
					user.setUserAppleKey(appleLogin.getUserIdentifier());
					user.setOauthType(OAuthType.APPLE.getCode());
					user = userService.save(user);
					appleLogin.setBasicInfo();
					appleLogin.setStatus(AppleLoginStatus.REG.getCode());
					appleLogin.setUserId(user.getUserId());
					userService.save(appleLogin);
					return user;
				}
			}else {
				User keyUser =new User();
				keyUser.setUserAppleKey(appleLogin.getUserIdentifier());
				user = isDbUserAppleKey(keyUser);
				if(user!=null) {
					appleLogin.setEmail(user.getEmail());
					appleLogin.setBasicInfo();
					appleLogin.setStatus(AppleLoginStatus.REG.getCode());
					appleLogin.setUserId(user.getUserId());
					userService.save(appleLogin);
					return user;
				}else {
					return null;
				}
			}
			
			appleLogin.setBasicInfo();
			appleLogin.setStatus(AppleLoginStatus.REG.getCode());
			user = new User();
			user.setEmail(appleLogin.getEmail());
			user.setUserAppleKey(appleLogin.getUserIdentifier());
			user.setOauthType(OAuthType.APPLE.getCode());
			user.setStatus(UserStatus.REG.getCode());
			user = userService.save(user);
			appleLogin.setUserId(user.getUserId());
			userService.save(appleLogin);
		} else {
			user = userService.getUser(dbAppleLogin.getUserId());
		}

		return user;
	}

	public User isDuplicate(String userName) {
		List<User> list = null;
		// 여러건 조회되면 오류 발생
		list = userService.getUserByUserName(userName);
		if (StringUtil.isEmpty(list)) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public void setUserId(String email) {
		User user = userService.getUserByEmail(email);
		cacheService.put(CacheKey.USER_EMAIL, email, user.getUserId());
	}

	public void setUserId(String email, String userid) {
		cacheService.put(CacheKey.USER_EMAIL, email, userid);
	}

	public String getUserId(String email) {
		String userid = cacheService.get(CacheKey.USER_EMAIL, email);
		return userid;
	}

	public User isAdminUser(User loginUser) {
		User user = userService.getUserByEmailAndOauthType(loginUser.getEmail(), loginUser.getOauthType());
		if (user == null)
			return null;

		if (StringUtil.isEmpty(user.getImageDir())) {
			user.setImageDir(loginUser.getImageDir());
			userService.save(user);
		}

		return user;

	}
//
//	public String getAppleUrl() {
//		String url = ISS + "/auth/authorize?client_id=" + AUD + "&redirect_uri=" + REDIRECT_URL
//				+ "&response_type=code id_token&response_mode=form_post";
//		return url;
//	}

}
