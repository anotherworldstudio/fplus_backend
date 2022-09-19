package com.kbeauty.gbt.dao;

import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.view.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserMapper2 {

	public List<UserListView2> getUserList(UserCondition2 condition);
	public int getUserListCnt(UserCondition2 condition);

	public int emailCheck(String email);


}
