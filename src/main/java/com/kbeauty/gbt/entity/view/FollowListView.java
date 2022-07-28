package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class FollowListView {	
	private long seq;
	private String followId;
	private String followType;	
	private String userId;
	private String followerId;	
	private String reqType;
	private String reqBlock;
	private String resType;
	private String resBlock;	
	private String followYn;	
	
	private String userName;	
	private String sex;	
	private String cellphone;
	private String email;
	private String imageDir;
	private String imageName;	
	private String status;
	private String statusName;	
	private String loginType;	
	private String imgUrl;
	
	private int together;
	private ImageData imgData;

}
