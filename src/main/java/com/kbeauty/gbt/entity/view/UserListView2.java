package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.domain.User2;
import lombok.Data;

@Data
public class UserListView2 {
	private long seq;
	private String name;
	private String userId;
	private String email;
	private String password;
	private String place;
	private String sex;
	private String birth;
	private String userRole;
	private String imageName;
	private String imageDir;
	private String loginTime;
	private String oauthType;
	private String status;
	private String imgUrl;
}
