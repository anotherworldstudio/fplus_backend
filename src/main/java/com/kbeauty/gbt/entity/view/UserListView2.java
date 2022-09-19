package com.kbeauty.gbt.entity.view;

import lombok.Data;

@Data
public class UserListView2 {
	private long seq;
	private String userId;
	private String password;
	private String email;
	private String userRole;
	private String friendId;
}
