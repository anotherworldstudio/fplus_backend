package com.kbeauty.gbt.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

public class KakaoApi extends DefaultApi20 {

    protected KakaoApi() {
    }

    private static class InstanceHolder {
        private static final KakaoApi INSTANCE = new KakaoApi();
    }

    public static KakaoApi instance() {
        return KakaoApi.InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://kauth.kakao.com/oauth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://kauth.kakao.com/oauth/authorize";
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }
    
    public String getInfoUrl() {
    	return "https://kapi.kakao.com/v2/user/me";
    }
}