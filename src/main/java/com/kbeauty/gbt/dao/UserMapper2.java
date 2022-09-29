package com.kbeauty.gbt.dao;

import com.kbeauty.gbt.entity.domain.User2;
import com.kbeauty.gbt.entity.view.*;

import java.util.List;

public interface UserMapper2 {

	public List<UserListView2> getUserList(UserCondition2 condition);
	public int getUserListCnt(UserCondition2 condition);

	public int emailCheck(String email);

	public User2 loginFplus(User2 user2) ;


}
