package com.kbeauty.gbt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FollowActionType;
import com.kbeauty.gbt.entity.enums.FollowSearchType;
import com.kbeauty.gbt.entity.enums.FollowType;
import com.kbeauty.gbt.entity.view.FollowCondition;
import com.kbeauty.gbt.entity.view.FollowListView;
import com.kbeauty.gbt.entity.view.UserCondition;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.util.StringUtil;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "Follow Controller")
@RequestMapping("/v1/follow")
public class FollowRestCtrl {
	
	@Autowired
	private UserService service;
	
	@ApiOperation(value = "getFollowListView", notes = "Follow 목록 조회")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/follow_list", method=RequestMethod.POST)
	public PagingList<FollowListView> getFollowListView(
			@ApiParam(value = "조회 조건", required = true )
			@RequestBody FollowCondition condition
			) {
		
		PagingList<FollowListView> list = new PagingList<>();
		if(condition == null) condition = new FollowCondition();
		
		if( StringUtil.isEmpty( condition.getUserId() ) ) {
			list.setError(ErrMsg.NO_USER_ID);			
			return list;
		}
		
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		List<FollowListView> userList = service.getFollowList(condition);
		if(userList == null || userList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		int totalCnt = service.getFollowListCnt(condition);
		paging.setTotalCount(totalCnt);		
		
		list.setList(userList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}
	
	@ApiOperation(value = "getFollowingListView", notes = "Following 목록 조회")
	@ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/following_list", method=RequestMethod.POST)
	public PagingList<FollowListView> getFollowingListView(
			@ApiParam(value = "조회 조건", required = true )
			@RequestBody FollowCondition condition
			) {
		PagingList<FollowListView> list = new PagingList<>();
		if(condition == null) condition = new FollowCondition();
		
		if( StringUtil.isEmpty( condition.getUserId() ) ) {
			list.setError(ErrMsg.NO_USER_ID);			
			return list;
		}
		
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		List<FollowListView> userList = service.getFollowingList(condition);
		if(userList == null || userList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		int totalCnt = service.getFollowingListCnt(condition);
		paging.setTotalCount(totalCnt);		
		
		list.setList(userList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}

	
	
	@ApiOperation(value = "requestFollowing", notes = "Following 요청 ")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/request", method=RequestMethod.POST)
	public Follow requestFollowing(HttpServletRequest request,
			@RequestBody Follow follow
			){
		
		if( StringUtil.isEmpty( follow.getUserId() ) ) {
			follow.setError(ErrMsg.NO_USER_ID);			
			return follow;
		}
		
		if( StringUtil.isEmpty( follow.getFollowerId() ) ) {
			follow.setError(ErrMsg.NO_FOLLOW_ID);			
			return follow;
		}
		
		if(follow.getUserId().equals(follow.getFollowerId())) {
			follow.setError(ErrMsg.SAME_FOLLOW);			
			return follow;
		}			
		
		
		String userId = TokenUtils.getTokenUserId(request);
		
		try {
			follow = service.saveFollow(follow, userId, FollowActionType.REQUEST);
		} catch (MessageException me) {
			follow.setError(me.getMsg());			
			return follow;
		}
		
		follow.setOk();
		
		return follow;
	}
	
	@ApiOperation(value = "confirmFollowing", notes = "Following 승인 ")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/confirm", method=RequestMethod.POST)
	public Follow confirmFollowing(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.CONFIRM);
	}
	
	@ApiOperation(value = "unFollow", notes = "언팔")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/unfollow", method=RequestMethod.POST)
	public Follow unFollowing(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.UNFOLLOWING);		
	}
	
	@ApiOperation(value = "cancelUnFollow", notes = "언팔 취소")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/cancel_unfollow", method=RequestMethod.POST)
	public Follow cancelUnFollowing(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.CANCEL_UNFOLLOWING);		
	}
	
	@ApiOperation(value = "deleteFollow", notes = "팔로우 삭제")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public Follow deleteFollow(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.DELETE);
	}
	
	@ApiOperation(value = "cancelDeleteFollow", notes = "팔로우 삭제 취소")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/cancel_delete", method=RequestMethod.POST)
	public Follow cancelDeleteFollow(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.CANCEL_DELETE);
	}
	
	@ApiOperation(value = "withdraw", notes = "Following 요청 철회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/withdraw", method=RequestMethod.POST)
	public Follow withdrawFollow(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.WITHDRAW);
	}
	
	@ApiOperation(value = "cancelWithdraw", notes = "Following 요청 철회")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/cancel_withdraw", method=RequestMethod.POST)
	public Follow cancelWithdraw(HttpServletRequest request,
			@RequestBody Follow follow
			){
		return saveFollow(request, follow, FollowActionType.CANCEL_WITHDRAW);
	}
	
	
	private Follow saveFollow(HttpServletRequest request, Follow follow, FollowActionType followActionType) {
		if( StringUtil.isEmpty( follow.getFollowId() ) ) {
			follow.setError(ErrMsg.NO_FOLLOW_ID);			
			return follow;
		}
		
		String userId = TokenUtils.getTokenUserId(request);
		
		try {
			follow = service.saveFollow(follow, userId, followActionType);
		} catch (MessageException me) {
			follow.setError(me.getMsg());			
			return follow;
		}
		
		follow.setOk();
		
		return follow;
	}
}
