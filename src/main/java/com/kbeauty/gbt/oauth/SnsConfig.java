package com.kbeauty.gbt.oauth;

import javax.servlet.http.HttpServletRequest;

public class SnsConfig {
	private HttpServletRequest request;
	 
    public String getHostName() {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
 
        return scheme + "://" + serverName;
    }
 
    public String getRedirectUrl(String snsType) {
        return getHostName() + "/oAuthCallback.do?m=" + snsType;
    }
 
    public SnsConfigParameter line() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter kakao() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter facebook() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter instagram() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter twitter() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter google() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter weibo() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    public SnsConfigParameter wechat() {
        return new SnsConfigParameter( /* ... */ );
    }
 
    private static SnsConfig instance = new SnsConfig();
 
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
 
    public static SnsConfig getInstance(HttpServletRequest request) {
        instance.setRequest(request);
 
        return instance;
    }
 
    public final static SnsConfigParameter FACEBOOK = new SnsConfigParameter( /* ... */ );
 
    public final static SnsConfigParameter INSTAGRAM = new SnsConfigParameter( /* ... */ );
 
    public final static SnsConfigParameter KAKAO = new SnsConfigParameter( /* ... */ );
 
    public final static SnsConfigParameter TWITTER = new SnsConfigParameter( /* ... */ );
 
    public final static SnsConfigParameter GOOGLE = new SnsConfigParameter( /* ... */ );
 
    public final static SnsConfigParameter WEIBO = new SnsConfigParameter( /* ... */ );
}
