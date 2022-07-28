package com.kbeauty.gbt.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.Checked;
import com.kbeauty.gbt.entity.enums.ClassType;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.Currency;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.OtherCode;
import com.kbeauty.gbt.entity.enums.OtherStatus;
import com.kbeauty.gbt.entity.enums.OtherType;
import com.kbeauty.gbt.entity.enums.PriceType;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ClassView;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.ProductService;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/product")
@Controller
@Slf4j
public class ProductCtrl {

	@Autowired
	ContentService service;
	
	@Autowired
	ProductService productService;

	@Resource
	private Login login;
	
	private final static String conditionKey = "ContentCondition";
	private final static String CONTENT_VIEW = "contentView";
	
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
	
	private ContentCondition getDefaultContentCondition() {
		ContentCondition condition = new ContentCondition();
		condition.setContentType(ContentType.PRODUCT.getCode());
		
		
		return condition;
	}
	
	private void goListCommon(HttpServletRequest request, ContentCondition condition, Model model) {
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = getDefaultContentCondition();						
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = getDefaultContentCondition();
				}		
			}
		}
		
		
		model.addAttribute("condition", condition);
	}
	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, ContentCondition condition, Model model) {		
		
		goListCommon(request, condition, model);
		
		return "admin/product/list";
	}
	
	@GetMapping("/go_list_popup")
	public String goListPopup(HttpServletRequest request, ContentCondition condition, Model model) {		
		
		goListCommon(request, condition, model);
		
		return "admin/product/list_popup";
	}

	
	@GetMapping("/go_create")
	public String goCreate(Model model) throws Exception{
		ContentView view = new ContentView();
		Content content = new Content();
		content.setReplyYn(YesNo.YES.getVal());
		content.setActive(Active.PASSIVE.getCode());
		content.setContentType(ContentType.PRODUCT.getCode());
		
		Price price = new Price();
		price.setPriceType(PriceType.PRODUCT.getCode());
		price.setCurr(Currency.KRW.getCode());
		
		List<Others> featureList = new ArrayList<>();
		OtherCode[] values = OtherCode.values();
		Others other = null;
		int i = 0;
		for (OtherCode otherCode : values) {
			i++;
			other = new Others();
			other.setOtherType(OtherType.FEATURE.getCode());
			other.setOtherCode(otherCode.getCode());
			other.setOtherName(otherCode.getVal());
			other.setOtherOrder(i);			
			other.setOtherStatus(OtherStatus.REG.getCode());
			other.setOtherValue(Checked.NO.getVal()); 
			featureList.add(other);
		}
				
		view.setContent(content);
		view.setPrice(price);
		view.setFeatureList(featureList);
						
		model.addAttribute(CONTENT_VIEW, view);		
		return "admin/product/create";
	}
	
	@ResponseBody
    @RequestMapping("/get_classdata")
    public List<ClassView> test2(){
		List<ClassView> classList = productService.getClassList(ClassType.PRODUCT.getCode());		
        return classList;
    }
	
	@ResponseBody
	@RequestMapping("/get_point_class_data")
	public List<ClassView> getPointClassData(){
		List<ClassView> classList = productService.getClassList(ClassType.POINT.getCode());		
		return classList;
	}
	
	@RequestMapping(value="/create", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String create(ContentView view,
			//@RequestParam("hashTag") String tag,		
			@RequestParam(value="mainUploadFile", required = false) MultipartFile mainFile,
			@RequestParam("fileName") List<MultipartFile> files,
			@RequestParam("resourceType") List<String> resourceType,
			@RequestParam("resourceTitle") List<String> resourceTitle,
			@RequestParam("resourceContent") List<String> resourceContent,
			@RequestParam("url") List<String> url,
			Model model
			) throws Exception{
		
		List<Resources> list = new ArrayList<>();
		
		String userId = login.getUserId();
		
		String contentId = service.getContentId();
		long contentSeq = service.getContentSeq();
		
		Content content = view.getContent();
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
		
		view.setResourceList(list);
		
		//대표 이미지 등록
		if( ! FileUtil.isEmpty(mainFile)) {
			service.createContentFile(mainFile, userId, content);
		}
		
		Price price = view.getPrice();
		price.setContentId(contentId);
				
		
		service.create(view);
		
		String nextPage = "redirect:/w1/product/detail/" + contentId;
		
		return nextPage;
	}
	

	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<ContentView> getContentListView(HttpServletRequest request, @RequestBody ContentCondition condition) {
		
		PagingList<ContentView> list = new PagingList<>();
		if(condition == null) condition = new ContentCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int perPageNum = condition.getPerPageNum();
		if(perPageNum == 0) {
			perPageNum = 10;
		}
		paging.setDisplayPageNum(perPageNum);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);
		
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
		
		return "admin/product/detail";
	}
	
	@RequestMapping(value="/save_content", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public String save(ContentView view, @RequestParam(value="mainUploadFile", required = false) MultipartFile mainFile) throws Exception{
		
		Content content = view.getContent();
		
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
//		dbcontent.setContentType (content.getContentType ());
		
		dbcontent.setCategory1(content.getCategory1       ());
		dbcontent.setCategory2(content.getCategory2       ());
				
		dbcontent.setTitle       (content.getTitle       ());
		dbcontent.setVendor       (content.getVendor       ());
//		dbcontent.setMainUrl       (content.getMainUrl       ());
		
		dbcontent.setContent     (content.getContent     ());
		dbcontent.setViewType    (content.getViewType    ());
		dbcontent.setDescription    (content.getDescription    ());
		
		dbcontent.setElement    (content.getElement    ());
		
		
//		dbcontent.setReplyYn     (content.getReplyYn     ());
//		dbcontent.setStartDate   (content.getStartDate   ());
//		dbcontent.setEndDate     (content.getEndDate     ());		
		dbcontent.setActive      (content.getActive      ());
		dbcontent.setStatus      (content.getStatus      ());
		
		if( ! FileUtil.isEmpty(mainFile)) {
		    service.createContentFile(mainFile, userId, dbcontent);
		}
		
		view.setContent(dbcontent); // DB Content로 변경한다. 
		
		view = service.saveContentView(view, userId);		
		view.setOk();		
		
		
		String nextPage = "redirect:/w1/product/detail/" + content.getContentId();
		return nextPage;
	}

	@RequestMapping(value="/delete_content", method=RequestMethod.POST)
	public String delete(ContentView view){
		Content content = view.getContent();
		
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
				
		String nextPage = "redirect:/w1/product/go_list";
		return nextPage;
	}
	
	@ResponseBody
	@RequestMapping(value="/check_del_product", method=RequestMethod.POST)
	public ContentView checkDelProduct(HttpServletRequest request, @RequestBody Content content) throws Exception{
		ContentView view = new ContentView();
		int resourcesCount= service.checkDelProduct(content);
		Content content2 = new Content();
		content2.setDepth(resourcesCount);
		view.setContent(content2);
		view.setOk();
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/add_peerproduct", method=RequestMethod.POST)
	public ContentView addPeerProduct(
			@RequestParam(value="contentId") String contentId,
			@RequestParam(value="productIds") List<String> productIds) throws Exception{
		
		ContentView view = new ContentView();
		
		String userId = login.getUserId();		
		List<Others> peerProductList = service.createPeerProduct(contentId, productIds, userId);
		
		view.setPeerProductList(peerProductList);
		view.setOk();
		
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/mod_peerproduct", method=RequestMethod.POST)
	public ContentView modifiyPeerProduct(
			@RequestParam(value="contentId") String contentId,
			@RequestParam(value="otherId")   String otherId,
			@RequestParam(value="productId") String productId) throws Exception{
		
		ContentView view = new ContentView();
		
		String userId = login.getUserId();		
		List<Others> peerProductList = service.modifyPeerProduct(contentId, otherId, productId, userId);		
		view.setPeerProductList(peerProductList);
		view.setOk();
		
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/del_peerproduct", method=RequestMethod.POST)
	public ContentView deletePeerProduct(
			@RequestParam(value="contentId") String contentId,
			@RequestParam(value="otherId")   String otherId) throws Exception{
		
		ContentView view = new ContentView();
		
		String userId = login.getUserId();		
		List<Others> peerProductList = service.deletePeerProduct(contentId, otherId, userId);		
		view.setPeerProductList(peerProductList);
		view.setOk();
		
		return view;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/add_productimg", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public ContentView addProductImg(Others other, 
			@RequestParam(value="prodcutImgFiles") List<MultipartFile> prodcutImgFiles) throws Exception{
		
		ContentView view = new ContentView();
		
		String contentId = other.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}
		
		String userId = login.getUserId();		
		List<Others> productImgList = service.createProductImgFile(prodcutImgFiles, userId, contentId);
		view.setProductImgList(productImgList);
		view.setOk();
		
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/mod_productimg", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public ContentView modifyProductImg(Others other, 
			@RequestParam(value="prodcutImgFiles") List<MultipartFile> prodcutImgFiles) throws Exception{
		
		ContentView view = new ContentView();
		
		String contentId = other.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}
		
		String otherId = other.getOtherId();
		if(StringUtil.isEmpty(otherId)) {
			view.setError(ErrMsg.PRODUCT_NO_IMG_ERR);
			return view;
		}
		
		
		String userId = login.getUserId();		
		List<Others> productImgList = service.modifiyProductImgFile(prodcutImgFiles.get(0), userId, contentId, otherId);
		
		view.setProductImgList(productImgList);
		view.setOk();
		
		return view;
	}
	
	@ResponseBody
	@RequestMapping(value="/del_productimg", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")
	public ContentView delProductImg(Others other) throws Exception{
		
		ContentView view = new ContentView();
		
		String contentId = other.getContentId();
		
		if(StringUtil.isEmpty(contentId)) {
			view.setError(ErrMsg.CONTENT_NO_SAVE_ERR);
			return view;
		}
		
		String otherId = other.getOtherId();
		if(StringUtil.isEmpty(otherId)) {
			view.setError(ErrMsg.PRODUCT_NO_IMG_ERR);
			return view;
		}
		
		
		String userId = login.getUserId();		
		List<Others> productImgList = service.deleteProductImgFile(userId, contentId, otherId);
			
		view.setProductImgList(productImgList);
		view.setOk();
		
		return view;
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
