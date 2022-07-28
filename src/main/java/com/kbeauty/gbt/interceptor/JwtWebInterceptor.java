package com.kbeauty.gbt.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.util.TokenUtils;

@Component
public class JwtWebInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		if (TokenUtils.isValidToken(request)) {
			return true;
		} else {
		    response.sendRedirect("/error/unauthorizedweb");
		}
		
		return false;
	}
}
