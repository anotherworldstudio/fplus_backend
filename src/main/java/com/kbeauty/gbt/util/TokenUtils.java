package com.kbeauty.gbt.util;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenUtils {
	
	public final static String LOGIN_USER_KEY = "__userId__";
	
	private static String secretKey;
	
    @Value("${jwt-secret}")
    private void setValue(String secretKey){
        this.secretKey = secretKey;
    }

	public static String generateJwtToken(User user) {
		JwtBuilder builder = Jwts.builder()
				.setHeader(createHeader())
				.setClaims(createClaims(user))				
				.signWith(SignatureAlgorithm.HS256, createSigningKey());

		return builder.compact();
	}
	
	public static void setJwtHeader(HttpServletResponse response, User user) {
		String jwtToken = generateJwtToken(user);		
		response.addHeader(CommonConstants.HEADER_AUTH, CommonConstants.TOKEN_TYPE + " " + jwtToken);
	}
	
	public static void setJwtHeader(HttpServletRequest request, User user) {
		String jwtToken = generateJwtToken(user);		
		HttpSession session = request.getSession();
		session.setAttribute(CommonConstants.HEADER_AUTH, CommonConstants.TOKEN_TYPE + " " + jwtToken);
	}
	
	public static User getUser(HttpServletRequest request) {
		User user = null;
		String header = request.getHeader(CommonConstants.HEADER_AUTH);

		if (header == null) {
			return user;
		}		
		String token = TokenUtils.getTokenFromHeader(header);
		try {
			Claims claims = getClaimsFormToken(token);
			
			log.info("expireTime :" + claims.getExpiration());
			log.info("userId :" + claims.get("userId"));
			log.info("email :" + claims.get("email"));
			log.info("status :" + claims.get("status"));
			user = new User();
			user.setUserId((String)claims.get("userId"));
			user.setEmail((String)claims.get("email"));
			user.setStatus((String)claims.get("status"));
			
			return user;
		} catch (ExpiredJwtException exception) {
			log.error("Token Expired");		
		} catch (JwtException exception) {
			log.error("Token Tampered");			
		} catch (NullPointerException exception) {
			log.error("Token is null");
		}
		return user;
	}
	
	public static boolean isValidToken(HttpServletRequest request) {
		String header = request.getHeader(CommonConstants.HEADER_AUTH);
		
		log.debug("========= HEADER ================");
		log.debug(header);
		log.debug("=========================");

		if (header == null) {
			HttpSession session = request.getSession();
			header = (String)session.getAttribute(CommonConstants.HEADER_AUTH);
			
			log.debug("========= SESSION ================");
			log.debug(header);
			log.debug("=========================");
			
			if (header == null) {				
				log.info("header 가 정상적이지 않습니다.");
				return false;
			}
		}
		
		String token = TokenUtils.getTokenFromHeader(header);		
		String userId = getUserIdFromToken(token);
		if(userId != null) {
			request.setAttribute(LOGIN_USER_KEY, userId);
			return true;
		}else {
			return false;
		}
	}
	
	public static String getTokenUserId(HttpServletRequest request) {
		return (String)request.getAttribute(LOGIN_USER_KEY);
	}

	public static String getUserIdFromToken(String token) {
		try {			
			Claims claims = getClaimsFormToken(token);
						
			log.info("expireTime :" + claims.getExpiration());
			log.info("userId :" + claims.get("userId"));
			log.info("email :" + claims.get("email"));
			log.info("status :" + claims.get("status"));
			
			String email = (String)claims.get("email");
			if(email == null) return null;;
			
			if(email.equals(claims.getSubject())) {
				return (String)claims.get("userId");
			}
			
		} catch (ExpiredJwtException exception) {
			log.error("Token Expired");
			return null;
		} catch (JwtException exception) {
			log.error("Token Tampered");
			return null;
		} catch (NullPointerException exception) {
			log.error("Token is null");
			return null;
		}
		return null;
	}

	public static String getTokenFromHeader(String header) {
		return header.split(" ")[1];
	}

	private static Date createExpireDateForOneYear() {
		// 토큰 만료시간은 360일으로 설정
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 360);
		return c.getTime();
	}

	private static Map<String, Object> createHeader() {
		Map<String, Object> header = new HashMap<>();

		header.put("typ", "JWT");
		header.put("alg", "HS256");
		header.put("regDate", System.currentTimeMillis());

		return header;
	}

	private static Claims createClaims(User user) {
		
		Claims claims = Jwts.claims()
		        .setSubject(user.getEmail())
		        .setIssuedAt(new Date())
		        .setExpiration(createExpireDateForOneYear()); //만료일 설정

		claims.put("userId",  user.getUserId());
		claims.put("email",  user.getEmail());
        claims.put("status", user.getStatus());
        claims.put("userRole", user.getUserRole());

		return claims;
	}

	private static Key createSigningKey() {
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
	}

	private static Claims getClaimsFormToken(String token) {
		Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token);
		
		return parseClaimsJws.getBody();
	}


}
