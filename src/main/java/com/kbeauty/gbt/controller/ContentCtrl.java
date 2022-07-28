package com.kbeauty.gbt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/content")
@Controller
@Slf4j
public class ContentCtrl {

	@Autowired
	ContentService service;

	@Resource
	private Login login;
	
	private final static String conditionKey = "ContentCondition";

	private void setCondition(HttpServletRequest request, ContentCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}
	
	private ContentCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ContentCondition condiiton = (ContentCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, ContentCondition condition, Model model) {		
		
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = new ContentCondition();
			condition.setContentType(ContentType.FEED.getCode());
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = new ContentCondition();
					condition.setContentType(ContentType.FEED.getCode());
				}		
			}
		}
		
		
		model.addAttribute("condition", condition);
		
		return "admin/content/list";
	}
	
	@GetMapping("/go_create")
	public String goCreate() {
		return "admin/content/create";
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String create(Content content, 
			@RequestParam("mainUploadFile") MultipartFile mainFile,
			@RequestParam("hashTag") String tag,
			@RequestParam("fileName") List<MultipartFile> files,
			@RequestParam("resourceType") List<String> resourceType,
			@RequestParam("resourceTitle") List<String> resourceTitle,
			@RequestParam("resourceContent") List<String> resourceContent,
			@RequestParam("url") List<String> url,
			Model model
			) throws Exception{
		
		List<Resources> list = new ArrayList<>();
//		Resources res = null;
//		
//		for (MultipartFile file : files) {
//			log.debug(file.getName());
//		}
		
		String userId = login.getUserId();
		
		String contentId = service.getContentId();
		long contentSeq = service.getContentSeq();
		content.setContentId(contentId);
		content.setSeq(contentSeq);
		content.setOwnerId(userId);
		
		//Web에서 첫번째 행은 널 값을 가지고 온다.
		if(! StringUtil.isEmpty(resourceContent) && resourceContent.size() > 1) {			
			files.remove(0);
			url.remove(0);
			resourceType.remove(0);
			resourceContent.remove(0);
			resourceTitle.remove(0);
			list = service.createFiles(files, userId, contentId, resourceTitle, resourceContent, url);
		}
		
		if( ! FileUtil.isEmpty(mainFile)) {
			service.createContentFile(mainFile, userId, content);
		}
		
		ContentView view = new ContentView();
		view.setContent(content);
		view.setResourceList(list);
		
		service.create(view);
		
		String nextPage = "redirect:/w1/content/detail/" + contentId;
		
		return nextPage;
	}
	

	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<ContentView> getContentListView(HttpServletRequest request, @RequestBody ContentCondition condition) {
		
		PagingList<ContentView> list = new PagingList<>();
		if(condition == null) condition = new ContentCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		setCondition(request, condition);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);
		
//		List<ContentView> contentList = service.getContentViewList(condition);
		List<ContentView> contentList = service.getContentViewListNotAnother(condition);
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
	
	@RequestMapping(value="/delete_mainimg")
	@ResponseBody
	public Content deleteManiImg(HttpServletRequest request, @RequestBody Content content) {
		
		if(content == null) {			
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return content;
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return content;
		}
		
		String userId = login.getUserId();
		
		//기존 content를 가지고 온다.
		Content dbcontent = service.getContent(contentId);
		dbcontent.setMainDir("");
		dbcontent.setMainFilename("");
		dbcontent.setMainUrl("");
		
		content = service.saveContent(dbcontent, userId);		
		content.setOk();
		
		content.setMainUrl(ContentService.NO_CONTENT_IMG_PATH);
		
		return content;
	}	
	
	@RequestMapping(value="/detail/{contentId}", method=RequestMethod.GET)
	public String getContentView(
			@PathVariable("contentId") String contentId,
			Model model
			) { 
		
		String userId = login.getUserId();
		
		ContentView contentView = service.getContentView(contentId, userId);
		if(contentView == null) {
			contentView = new ContentView();
			contentView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		contentView.setOk();
		
		model.addAttribute("contentView", contentView);
		
		return "admin/content/detail";
	}
	
	@RequestMapping(value="/save_content", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String save(Content content, @RequestParam("mainUploadFile") MultipartFile mainFile) throws Exception{
		
		if(content == null) {			
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();
		//
		
		//기존 content를 가지고 온다.
		Content dbcontent = service.getContent(contentId);
		dbcontent.setContentType (content.getContentType ());
		dbcontent.setTitle       (content.getTitle       ());
		dbcontent.setContent     (content.getContent     ());
		dbcontent.setViewType    (content.getViewType    ());
		dbcontent.setReplyYn     (content.getReplyYn     ());
		dbcontent.setStartDate   (content.getStartDate   ());
		dbcontent.setEndDate     (content.getEndDate     ());		
		dbcontent.setActive      (content.getActive      ());
		dbcontent.setStatus      (content.getStatus      ());
		dbcontent.setOrders      (content.getOrders      ());
		
		
		dbcontent.setVendor      (content.getVendor      ());
		dbcontent.setGeoName     (content.getGeoName     ());
		dbcontent.setMainLink    (content.getMainLink    ());
		if( ! FileUtil.isEmpty(mainFile)) {
		    service.createContentFile(mainFile, userId, dbcontent);
		}
		
		content = service.saveContent(dbcontent, userId);
		
		content.setOk();		
		
		String nextPage = "redirect:/w1/content/detail/" + content.getContentId();
		return nextPage;
	}
	

	
	@RequestMapping(value="/delete_content", method=RequestMethod.POST)
	public String delete(Content content){
		
		if(content == null) {			
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		String contentId = content.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			content.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return "error";
		}
		
		String userId = login.getUserId();		
		content = service.deleteContent(content, userId);		
		content.setOk();
				
		String nextPage = "redirect:/w1/content/go_list";
		return nextPage;
	}
	
	@RequestMapping(value="/add_reply", method=RequestMethod.POST)
	@ResponseBody
	public ContentView addReply(Content content){
		
		ContentView view = new ContentView();
		
		if(content == null) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}		
		
		String userId = login.getUserId();		
		
		view = service.addReply(content, userId);
		
		if(! view.isNotOk()) {		
			view.setOk();
		}
		
		return view;
	}
	
	@RequestMapping(value="/modiy_reply", method=RequestMethod.POST)
	@ResponseBody
	public ContentView modifyReply(Content content){
		
		ContentView view = new ContentView();
		
		if(content == null) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}		
		
		String userId = login.getUserId();		
		
		view = service.modifyReply(content, userId);
		
		if(! view.isNotOk()) {		
			view.setOk();
		}
		
		return view;
	}
	
	@RequestMapping(value="/delete_reply", method=RequestMethod.POST)
	@ResponseBody
	public ContentView deleteReply(Content content){ 
		ContentView view = new ContentView();
		
		if(content == null) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}		
		
		String userId = login.getUserId();		
		
		view = service.deleteReply(content, userId);
		 
		if(! view.isNotOk()) {		
			view.setOk();
		}
		
		return view;
	}

	@RequestMapping(value="/reply_list")
	@ResponseBody
	public PagingList<ContentView> getReplyListView(HttpServletRequest request, @RequestBody ContentCondition condition) { 
		
		PagingList<ContentView> list = new PagingList<>();
		if(condition == null) condition = new ContentCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int currPage = condition.getPage(); 
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);
		
		setCondition(request, condition);
		
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
	
}
