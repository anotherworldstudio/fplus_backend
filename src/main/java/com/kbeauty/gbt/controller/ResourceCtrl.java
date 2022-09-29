package com.kbeauty.gbt.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.kbeauty.gbt.service.S3Uploader;
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
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/resource")
@Controller
@Slf4j
public class ResourceCtrl {

	@Autowired
	ContentService service;

	@Autowired
	S3Uploader s3Uploader;

	@Resource
	private Login login;

	private final static String conditionKey = "ContentCondition";

	private void setCondition(HttpServletRequest request, ContentCondition condition) {
		// TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}

	private ContentCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ContentCondition condiiton = (ContentCondition) session.getAttribute(conditionKey);
		return condiiton;
	}

	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, ContentCondition condition, Model model) {

		if (condition == null || condition.isEmpty()) {
			condition = getCondition(request);
			if (condition == null) {
				condition = new ContentCondition();
				condition.setContentType(ContentType.FEED.getCode());
			}
		}
		
		model.addAttribute("condition", condition);

		return "admin/content/list";
	}

	@GetMapping("/go_create")
	public String goCreate() {

		return "admin/content/create";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	public String create(Content content, @RequestParam("hashTag") String tag,
			@RequestParam("resourceType") List<String> resourceType,
			@RequestParam("resourceTitle") List<String> resourceTitle,
			@RequestParam("resourceContent") List<String> resourceContent, @RequestParam("url") List<String> url,
			@RequestParam("fileName") List<MultipartFile> files, Model model) throws Exception {

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

		// Web에서 첫번째 행은 널 값을 가지고 온다.
		files.remove(0);
		url.remove(0);
		resourceType.remove(0);
		resourceContent.remove(0);

		list = service.createFiles(files, userId, contentId, resourceTitle, resourceContent, url);

		ContentView view = new ContentView();
		view.setContent(content);
		view.setResourceList(list);

		service.create(view);

		String nextPage = "redirect:/w1/content/detail/" + contentId;

		return nextPage;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, headers = "Content-Type=multipart/form-data")
	@ResponseBody
	public Resources save(@RequestParam(value = "fileName", required = false) MultipartFile file, Resources resource) throws IOException {

		//String s3url = s3Uploader.upload(file,"test");
//		S3Uploader up = new S3Uploader();
//		up.upload(nnn,"user")
		if (resource == null) {
			resource = new Resources();
			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return resource;
		}
		String contentId = resource.getContentId();

		if (StringUtil.isEmpty(contentId)) {
			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return resource;
		}	

		String userId = login.getUserId();

		try {
			if (resource.getSeq() == 0) { // 신규 (수정이면 시퀀스가 있음)
				resource = service.addResource(file, resource, userId);
			} else {
				resource = service.saveResource(file, resource, userId);
			}
		} catch (IOException e) {
			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return resource;
		}
		

		resource.setOk();

		return resource;
	}

	// @RequestParam(value = "searchKeyWord1", required = false) String
	// searchKeyWord1,
	// @RequestParam(value = "writer", defaultValue = "MangKyu") String
	// searchKeyWord2

	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public String delete(@RequestParam(value = "contentId") String contentId,
			@RequestParam(value = "deleteResourceId") String resourceId,
			@RequestParam(value = "pageName" ) String pageName) {

//		if(resource == null) {			
//			resource.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
//			return "error";
//		}
//		String contentId = resource.getContentId();
//		String resourceId = resource.getResourceId();

		String userId = login.getUserId();
		Resources resource = service.deleteResource(resourceId, userId);
		resource.setOk();

		String nextPage = "redirect:/w1/"+pageName+"/detail/" + contentId;
		return nextPage;
		
	}
	
}
