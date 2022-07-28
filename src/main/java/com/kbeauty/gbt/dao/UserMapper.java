package com.kbeauty.gbt.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.view.FollowCondition;
import com.kbeauty.gbt.entity.view.FollowListView;
import com.kbeauty.gbt.entity.view.ModelCondition;
import com.kbeauty.gbt.entity.view.ModelUserList;
import com.kbeauty.gbt.entity.view.UserCondition;
import com.kbeauty.gbt.entity.view.UserListView;

public interface UserMapper {

	public List<UserListView> getUserList(UserCondition condition);
	public int getUserListCnt(UserCondition condition);
	
	public List<UserListView> searchUserName(UserCondition condition);
	
	
	public List<Map<String, String>> getUserMap(Map<String, List<String>> condition);
	
	
	
	public List<UserListView> getUserObjMap(Map<String, List<String>> condition);
	
	
	public Follow getOldFollowByUserId( Follow condition);
	
	public Follow getBlockByUserId(@Param("userId") String userId, @Param("blockUserid") String blockUserId);
	
	
//	public List<Set<String>> getFollowSet(Map<String, List<String>> condition);	
//	public List<Set<String>> getFollowingSet(Map<String, List<String>> condition);
	
	public Set<String> getFollowSet(Map<String, Object> condition);	
	public Set<String> getFollowingSet(Map<String, Object> condition);
	
	
	public List<FollowListView> getFollowList(FollowCondition condition);
	public int getFollowListCnt(FollowCondition condition);
	
	public List<FollowListView> getFollowingList(FollowCondition condition);
	public int getFollowingListCnt(FollowCondition condition);
	
	
	public int getFollowingCnt(UserCondition condition);
	public int getFollowCnt(UserCondition condition);
	
	public int getLikeCnt(UserCondition condition);

	
	public List<ModelUserList> getModelEventList(ModelCondition condition);
	public int getModelEventListCnt(ModelCondition condition);
	public List<UserFace> getUserFaceList(UserCondition condition);
	
	public User findByUserAppleKey(User user);
	
	
}
