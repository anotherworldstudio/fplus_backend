package com.kbeauty.gbt.controller;



import org.springframework.http.HttpHeaders;

import com.kbeauty.gbt.entity.CommonConstants;

public class CommonCtrlTest {
	
	protected HttpHeaders getHeader() {
		String jwt = "eyJyZWdEYXRlIjoxNjEwNzE1NDI4NTI1LCJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJiZXRhamlub0BnbWFpbC5jb20iLCJpYXQiOjE2MTA3MTU0MjgsImV4cCI6MTY0MTgxOTQyOCwidXNlcklkIjoiZDkwNDA2MjI4M2VhNDY0ZTgzYmM4ZThhYTMxMGE2ZTUiLCJlbWFpbCI6ImJldGFqaW5vQGdtYWlsLmNvbSIsInN0YXR1cyI6IjIwMDAifQ.Ciz-J4t80BnhJm_nfRlPQVGHhLKbhWgy_gJHrChZeOI";

		HttpHeaders headers = new HttpHeaders();
	    headers.add(CommonConstants.HEADER_AUTH, CommonConstants.TOKEN_TYPE + " " + jwt);
	    
	    return headers;
	}

}
