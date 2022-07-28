package com.kbeauty.gbt.entity.view;

import javax.persistence.Transient;

import lombok.Data;

@Data
public class UserListView {
	private long seq;
	private String userId;
	private String userName;
	private String birthDay;
	private String sex;
	private String country;
	private String cellphone;
	private String email;
	private String addreName;
	private String addreType;
	private String zipcode;
	private String addre;
	private String addreDetail;
	private String userRole;
	private String userRoleName;
	private String imageDir;
	private String imageName;
	private String comment;
	private String language;
	private String oauthType;
	private String status;
	private String statusName;	
	private String keepLoginyn;
	private String agreeYn;
	private String privateYn;
	private String ageoverYn;
	private String marketingYn;
	private String loginType;	
	private String imgUrl;
	private ImageData imgData;
	private int totalMileage;
	private int totalExp;

}
