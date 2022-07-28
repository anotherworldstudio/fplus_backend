package com.kbeauty.gbt.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.LikeType;
import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.util.StringUtil;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Feed Controller")
@RequestMapping("/v1/feed")
@Slf4j
public class FeedRestCtrl {
	
	@Autowired
	ContentService service;

	@ApiOperation(value = "getContentView", notes = "Content 조회함수")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/{contentId}", method=RequestMethod.GET)
	public ContentView getContentView(HttpServletRequest request,			
			@ApiParam(value = "ContentID", required = false, example = "Content GUID")
			@PathVariable("contentId") String contentId
			) { 
		
		String userId = TokenUtils.getTokenUserId(request);
		
		ContentView contentView = service.getContentView(contentId, userId);
		if(contentView == null) {
			contentView = new ContentView();
			contentView.setError(ErrMsg.NO_RESULT);
			return contentView;
		}
		contentView.setOk();
		return contentView;
	}
	
	@ApiOperation(value = "save", notes = "Feed 데이터 저장")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/create", method=RequestMethod.POST)
	public ContentView create(
			@ApiParam(value = "Feed 정보", required = true, example="email,username,agreeyn,privateyn,ageoveryn")
			@RequestBody ContentView contentView
			){
		
		Content content = contentView.getContent();
		if(content == null) {			
			contentView.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return contentView;
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			contentView.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return contentView;
		}
		
		contentView = service.create(contentView);
		
		contentView.setOk();
		return contentView;
	}
		
	@ApiOperation(value = "save", notes = "Feed 데이터 저장")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/save", method=RequestMethod.POST)
	public ContentView save(
			@ApiParam(value = "Feed 정보", required = true, example="email,username,agreeyn,privateyn,ageoveryn")
			@RequestBody ContentView contentView
			){
		
		Content content = contentView.getContent();
		if(content == null) {			
			contentView.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return contentView;
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			contentView.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return contentView;
		}
		
		contentView = service.create(contentView);
		
		contentView.setOk();
		return contentView;
	}
	
	@ApiOperation(value = "upload_contents", notes = "다건 Feed 파일 업로드")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })	
	@RequestMapping(value="/upload_contents", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public CommonView fileUploads(@RequestPart List<MultipartFile> files, 
								  @RequestParam String userId, 
								  @RequestParam String contentId,
								  @RequestParam List<String> resourceTitles,
								  @RequestParam List<String> resourceContents,
								  @RequestParam List<String> urls
			){
		CommonView view = new CommonView();
		try { 
			service.createFiles(files, userId, contentId, resourceTitles, resourceContents, urls);			
		} catch (IOException e) {
			view.setError(ErrMsg.CONTENT_IMG_ERR);
			return view;
		}
		view.setOk();
		return view;  
	}
	
	@ApiOperation(value = "upload_content", notes = "단일 Feed 파일 업로드")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})	
	@RequestMapping(value="/upload_content", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public CommonView fileUpload(@RequestPart MultipartFile file, @RequestParam String userId, @RequestParam String contentId,@RequestParam String title, @RequestParam String content, @RequestParam String url) {
		
		CommonView view = new CommonView();
		try {									
			service.createFile(file, userId, contentId, title, content, url);									
		} catch (IOException e) {
			view.setError(ErrMsg.CONTENT_IMG_ERR);
			return view;
		}
		view.setOk();
		return view;
	}
	
	@ApiOperation(value = "saveLike", notes = "Like 저장")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/save_like", method=RequestMethod.POST)
	public Likes saveLike(
			@ApiParam(value = "Like 정보", required = true)
			@RequestBody Likes like
			){
		
		like.setLikeType(LikeType.LIKE.getCode());		
		like.setOk();
		service.saveLikes(like);
		return like;
	}
	
	@ApiOperation(value = "deleteLike", notes = "Like 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK !!")
    })
	@RequestMapping(value="/delete_like", method=RequestMethod.POST)
	public Likes deleteLike(
			@ApiParam(value = "Like 정보", required = true)
			@RequestBody Likes like
			){		
		like.setOk();
		service.deleteFlagLikes(like);
		return like;
	}
	
	@ApiOperation(value = "saveFavorite", notes = "즐겨찾기 저장")
	@ApiResponses({
		@ApiResponse(code = 200, message = "OK !!")
	})
	@RequestMapping(value="/save_fav", method=RequestMethod.POST)
	public Likes saveFavorite(
			@RequestBody Likes like
			){
		like.setLikeType(LikeType.FAV.getCode());		
		like.setOk();
		service.saveLikes(like);
		
		return like;
	}
	
	@ApiOperation(value = "getContentListView", notes = "Content 목록 조회함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/list", method=RequestMethod.POST)
	public PagingList<ContentView> getContentListView(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true )
			@RequestBody ContentCondition condition
			) {
		
		PagingList<ContentView> list = new PagingList<>();
		if(condition == null) condition = new ContentCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		String userId = TokenUtils.getTokenUserId(request);
		if(StringUtil.isEmpty(condition.getSearchUserid())) {			
			condition.setSearchUserid(userId);
		}
		
		List<ContentView> contentList = service.getContentViewList(condition);
		if(contentList == null || contentList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getContentListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(contentList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}
	
	@ApiOperation(value = "getFollowContentViewList", notes = "팔로우 Content 목록 조회함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/follow_list", method=RequestMethod.POST)
	public PagingList<ContentView> getFollowContentViewList(HttpServletRequest request,
			@ApiParam(value = "조회 조건", required = true )
			@RequestBody ContentCondition condition
			) {
		
		PagingList<ContentView> list = new PagingList<>();
		if(condition == null) condition = new ContentCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		String userId = TokenUtils.getTokenUserId(request);
		if(StringUtil.isEmpty(condition.getSearchUserid())) {			
			condition.setSearchUserid(userId);
		}
		
		List<ContentView> contentList = service.getFollowContentViewList(condition);
		if(contentList == null || contentList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
		
		int totalCnt = service.getFollowContentListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(contentList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}
	
	@ApiOperation(value = "getReplyListView", notes = "댓글 목록 조회함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/reply_list", method=RequestMethod.POST)	
	public PagingList<ContentView> getReplyListView(HttpServletRequest request, 
													@ApiParam(value = "조회 조건", required = true )                                        
													@RequestBody ContentCondition condition) {  
		
		PagingList<ContentView> list = new PagingList<>();
		if(condition == null) condition = new ContentCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		String userId = TokenUtils.getTokenUserId(request);
		condition.setSearchUserid(userId);
		
		List<ContentView> contentList = service.getReplyList(condition);
		if(contentList == null || contentList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
				
		int totalCnt = service.getReplyListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(contentList);
		list.setPaging(paging);
		list.setOk();
		return list;
	}		
	
	@ApiOperation(value = "addReply", notes = "댓글 추가함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/add_reply", method=RequestMethod.POST)	
	public ContentView addReply(HttpServletRequest request, 
			@ApiParam(value = "댓글입력 내용", required = true )
			@RequestBody Content content){ 
		ContentView view = new ContentView();
		
		if(content == null) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}
		
		String userId = TokenUtils.getTokenUserId(request);
		
		view = service.addReply(content, userId);
		
		if(! view.isNotOk()) {		
			view.setOk();
		}
		
		return view;
	}
	
	@ApiOperation(value = "modifyReply", notes = "댓글 수정함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/modiy_reply", method=RequestMethod.POST)	
	public ContentView modifyReply(
			HttpServletRequest request, 
			@ApiParam(value = "댓글수정 내용", required = true )
			@RequestBody Content content){
		
		ContentView view = new ContentView();
		
		if(content == null) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}		
		
		String userId = TokenUtils.getTokenUserId(request);
		
		view = service.modifyReply(content, userId);
		
		if(! view.isNotOk()) {		
			view.setOk();
		}
		
		return view;
	}
	
	@ApiOperation(value = "deleteReply", notes = "댓글 삭제함수")
    @ApiResponses({ @ApiResponse(code = 200, message = "OK !!")} )
	@RequestMapping(value="/delete_reply", method=RequestMethod.POST)	
	public ContentView deleteReply(
			HttpServletRequest request, 
			@ApiParam(value = "댓글삭제", required = true )
			@RequestBody Content content){  
		ContentView view = new ContentView();
		
		if(content == null) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}		
		
		String userId = TokenUtils.getTokenUserId(request);
		
		view = service.deleteReply(content, userId);
		
		if(! view.isNotOk()) {		
			view.setOk();
		}
		
		return view;
	}
	
	@ApiOperation(value = "delete", notes = "Feed 삭제")
	@RequestMapping(value="/delete", method=RequestMethod.POST)	
	public Content delete(HttpServletRequest request, @RequestBody Content content){
		
		if(content == null) {			
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return content;
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return content;
		}
		
		String userId = TokenUtils.getTokenUserId(request);
		content = service.deleteContent(content, userId);		
		content.setOk();
				
		return content;
	}
	
	@ApiOperation(value = "saveResource", notes = "Resource 수정")
	@RequestMapping(value="/save_resource", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")	
	public Resources saveResource(HttpServletRequest request,
			          @RequestParam(value = "file", required = false) MultipartFile file,
			          Resources resource){
		
		if(resource == null) {			
			resource = new Resources();
			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return resource;
		}
		String contentId = resource.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return resource;			
		}
		
		String userId = TokenUtils.getTokenUserId(request);
		
		try {
			if(resource.getSeq() == 0) { //신규
				resource = service.addResource(file, resource, userId);
			}else {				
				resource = service.saveResource(file, resource, userId);
			}
		} catch (IOException e) {
			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return resource;			
		}
		
		resource.setOk();		
		
		return resource;
	}
	
	
}
