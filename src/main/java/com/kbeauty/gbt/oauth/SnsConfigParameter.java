package com.kbeauty.gbt.oauth;

import lombok.Data;

@Data
public class SnsConfigParameter {
	private String clientId;
    private String clientSecret;
    private String redirectUri;
}
