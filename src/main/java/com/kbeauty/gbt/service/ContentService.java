package com.kbeauty.gbt.service;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.dao.ContentMapper;
import com.kbeauty.gbt.dao.ContentRepo;
import com.kbeauty.gbt.dao.CountRepo;
import com.kbeauty.gbt.dao.FileInfoRepo;
import com.kbeauty.gbt.dao.LikeRepo;
import com.kbeauty.gbt.dao.OthersRepo;
import com.kbeauty.gbt.dao.PriceRepo;
import com.kbeauty.gbt.dao.ResourceRepo;
import com.kbeauty.gbt.dao.WeatherProductRepo;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.FileInfo;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.SkinResource;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.WeatherProduct;
import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.ContentActive;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FileInfoType;
import com.kbeauty.gbt.entity.enums.LikeType;
import com.kbeauty.gbt.entity.enums.OtherStatus;
import com.kbeauty.gbt.entity.enums.OtherType;
import com.kbeauty.gbt.entity.enums.PriceType;
import com.kbeauty.gbt.entity.enums.ResourceCategory;
import com.kbeauty.gbt.entity.enums.ResourceType;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.entity.view.SkinResultView;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.ImageUtil;
import com.kbeauty.gbt.util.MathUtil;
import com.kbeauty.gbt.util.StringUtil;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContentService {

	public final static String NO_CONTENT_IMG_PATH = "/images/no_user_img.png";
	public final static String NO_USER_IMG_PATH = "/images/no_user_img.png";

	public final static String PRODUCT_LIST_KEY = "productIdList";

	@Autowired
	private ContentRepo contentRepo;

	@Autowired
	private ResourceRepo resourceRepo;

	@Autowired
	private LikeRepo likeRepo;

	@Autowired
	private PriceRepo priceRepo;

	@Autowired
	private CountRepo countRepo;

	@Autowired
	private OthersRepo othersRepo;
	
	@Autowired
	private WeatherProductRepo weatherProductRepo;

	@Autowired
	private ContentMapper contentMapper;

	@Autowired
	private StorageService uploadSerivce;
	
	@Autowired
	private FileInfoRepo fileInfoRepo;

	@Autowired
	private UserService userService;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucket;

	@Value("${spring.cloud.gcp.storage.bucket.content}")
	private String contentFolder;

	@Value("${spring.cloud.gcp.storage.url}")
	private String storageUrl;

	
	private FileInfo getFileInfo(String type,String filename,String urlLink) {
		if(filename==null || "".equals(filename)) {
			return new FileInfo();
		}
		return getFileInfo(type, filename, urlLink,null);
	}
	private FileInfo getFileInfo(String type,String filename,String urlLink,String userId) {
		FileInfo fileInfo = fileInfoRepo.findByFilename(filename);
		if(fileInfo == null) {
			fileInfo = new FileInfo();
			fileInfo.setBasicInfo(userId);
			fileInfo.setFilename(filename);
			fileInfo.setType(type);
			if(filename!=null&&!"".equals(filename)) {
			}
			String array[] = filename.split("[.]");
			if (array[array.length - 1].length() < 10)
				fileInfo.setExtension(array[array.length - 1]);
			try {
				URL url = new URL(urlLink);
				Image image = ImageIO.read(url);
				fileInfo.setWidth(image.getWidth(null));
				fileInfo.setHeight(image.getHeight(null));
			} catch (Exception e) {
				// 이미지가아님
			}
			FileInfo fileInfo2 = fileInfoRepo.findByFilename(filename);
			if(fileInfo2 == null) {
				fileInfoRepo.save(fileInfo);
			}
		}else {
			
		}
		return fileInfo;
	}
	
	
	private Map<String, Others> getPeerProductInfoMapByOthers(List<Others> others) {
		List<String> productIds = new ArrayList<String>();
		for (Others other : others) {
			productIds.add(other.getOtherCode());
		}

		return getPeerProductInfoMap(productIds);
	}

	private Map<String, Others> getPeerProductInfoMap(List<String> productIds) {

		Map<String, List<String>> condition = new HashMap<String, List<String>>();
		condition.put(PRODUCT_LIST_KEY, productIds);

		List<Others> list = contentMapper.getPeerProductInfoMap(condition);

		Map<String, Others> peerProductInfoMap = new HashMap<>();
		for (Others other : list) {
			peerProductInfoMap.put(other.getOtherCode(), other);
		}

		return peerProductInfoMap;
		
	}

	/**
	 * 목록 조회용
	 * 
	 * @param condition
	 * @return
	 */

	
	public List<ContentView> getContentViewList(ContentCondition condition) {
		List<ContentView> list = new ArrayList<>();
		List<Content> contentList = contentMapper.getContentList(condition);

		Map<String, UserListView> userObjMap = getUserObjMap(contentList);

		for (Content content : contentList) {
			list.add(getContentViewByContent(content, userObjMap, condition.getSearchUserid()));
		}
		return list;
	}
	
	public List<ContentView> getContentViewListNotAnother(ContentCondition condition) {
		List<ContentView> list = new ArrayList<>();
		List<Content> contentList = contentMapper.getContentList(condition);

		Map<String, UserListView> userObjMap = getUserObjMap(contentList);

		for (Content content : contentList) {
			list.add(getContentViewByContentNotAnother(content, userObjMap, condition.getSearchUserid()));
		}
		return list;
	}

	public List<ContentView> getFollowContentViewList(ContentCondition condition) {
		List<ContentView> list = new ArrayList<>();
		List<Content> contentList = contentMapper.getFollowContentList(condition);

		Map<String, UserListView> userObjMap = getUserObjMap(contentList);

		for (Content content : contentList) {
			list.add(getContentViewByContent(content, userObjMap, condition.getSearchUserid()));
		}
		return list;
	}

	public int getContentListCnt(ContentCondition condition) {
		return contentMapper.getContentListCnt(condition);
	}

	public int getFollowContentListCnt(ContentCondition condition) {
		return contentMapper.getFollowContentListCnt(condition);
	}

	public List<ContentView> getReplyList(ContentCondition condition) {

		String contentId = condition.getContentId();
		Content mainContent = getContent(contentId);
		List<ContentView> list = new ArrayList<>();
		if (mainContent == null) {
			return list;
		}

		String relpyPath = StringUtil.getRelpyPath(mainContent.getSeq());
		condition.setPath(relpyPath);

		List<Content> replyList = contentMapper.getReplyList(condition);

		Map<String, UserListView> userObjMap = getUserObjMap(replyList);

		for (Content reply : replyList) {
			list.add(getContentViewByContent(reply, userObjMap, condition.getSearchUserid()));
		}
		return list;
	}

	public int getReplyListCnt(ContentCondition condition) {
		return contentMapper.getReplyListCnt(condition);
	}

	public String getContentId() {
		return CommonUtil.getGuid();
	}

	public Content getContent(String contentId) {
		Content content = contentRepo.findByContentId(contentId);
		return content;
	}

	public Resources getResource(String resourceId) {
		Resources resource = resourceRepo.findByResourceId(resourceId);
		return resource;
	}

	private void setContentUserInfo(Content content) {
		User user = userService.getUser(content.getOwnerId());
		if (user != null) {
			content.setOwnerName(user.getUserName());
			String ownerImgUrl = userService.getUrl(user);
			content.setOwnerImgUrl(ownerImgUrl);
		}
	}

	public ContentView addReply(Content content, String userId) {
		ContentView view = new ContentView();
		if (StringUtil.isEmpty(userId)) {
			view.setError(ErrMsg.NO_USER_ID);
			return view;
		}

		if (StringUtil.isEmpty(content.getContent())) {
			view.setError(ErrMsg.CONTENT_DATA_ID);
			return view;
		}

		if (StringUtil.isEmpty(content.getUpperContentId())) {
			view.setError(ErrMsg.CONTENT_UPPER_ID);
			return view;
		}

		if (StringUtil.isEmpty(content.getOwnerId())) {
			content.setOwnerId(userId);
		}

		content.setViewType(ContentViewType.ALL.getCode());
		content.setReplyYn(YesNo.YES.getVal());
		content.setActive(Active.ACTIVE.getCode());
		content.setStatus(ContentStatus.REG.getCode());

		view.setContent(content);
		view = create(view);
		setContentUserInfo(content);

		String upperContentId = content.getUpperContentId();
		Content upperContent = getContent(upperContentId);

		modifyReviewGrade(upperContent);

		// 상위 게시물로 변경 필요
		setCount(upperContentId, LikeType.REPLY, true);

		return view;
	}

	private void modifyReviewGrade(Content upperContent) {
		float avgReviewGrade = getAvgReviewGrade(upperContent);
		upperContent.setReviewGrade(avgReviewGrade);
		save(upperContent);
	}

	public ContentView modifyReply(Content content, String userId) {
		ContentView view = new ContentView();
		String contentId = content.getContentId();
		if (StringUtil.isEmpty(contentId)) {
			view.setError(ErrMsg.CONTENT_NO_ID);
			return view;
		}

		if (StringUtil.isEmpty(userId)) {
			view.setError(ErrMsg.NO_USER_ID);
			return view;
		}

		String content2 = content.getContent();
		if (StringUtil.isEmpty(content2)) {
			view.setError(ErrMsg.CONTENT_DATA_ID);
			return view;
		}

		Content dbContent = getContent(contentId);
		dbContent.setContent(content2);

		Float reviewGrade = content.getReviewGrade();
		if (reviewGrade != null) {
			dbContent.setReviewGrade(reviewGrade);
		}

		Content saveContent = saveContent(dbContent, userId);

		String path = dbContent.getPath();
		String parentPath = StringUtil.getParentPath(path);
		Content parentContent = contentRepo.findByPath(parentPath);
		modifyReviewGrade(parentContent);

		setContentUserInfo(saveContent);
		view.setContent(saveContent);

		return view;
	}

	public ContentView deleteReply(Content content, String userId) {
		ContentView view = new ContentView();
		String contentId = content.getContentId();
		if (StringUtil.isEmpty(contentId)) {
			view.setError(ErrMsg.CONTENT_NO_ID);
			return view;
		}

		if (StringUtil.isEmpty(userId)) {
			view.setError(ErrMsg.NO_USER_ID);
			return view;
		}

		Content deleteContent = deleteContent(content, userId);

		String path = deleteContent.getPath();
		String parentPath = StringUtil.getParentPath(path);
		Content parentContent = contentRepo.findByPath(parentPath);
		modifyReviewGrade(parentContent);

		view.setContent(deleteContent);

		setCount(parentContent.getContentId(), LikeType.REPLY, false);

		return view;
	}

	/**
	 * Content를 생성한다.
	 * 
	 * @param ContentView
	 * @return ContentView
	 */
	public ContentView create(ContentView view) {
		String dateStr = CommonUtil.getSysTime();
		Content content = view.getContent();
		String contentId = content.getContentId();
		if (StringUtil.isEmpty(contentId)) {
			contentId = CommonUtil.getGuid();
			content.setContentId(contentId);
		}

		String upperContentId = content.getUpperContentId();
		Content upperContent = null;

		int upperDepth = 0;
		String upperPath = null;
		if (StringUtil.isEmpty(upperContentId)) {
			upperPath = "";
			upperDepth = -1;
		} else {
			upperContent = contentRepo.findByContentId(upperContentId);
			upperDepth = upperContent.getDepth();
			upperPath = upperContent.getPath();
		}

		long seq = getContentSeq();
		String userId = content.getOwnerId();

		content.setSeq(seq);

		if (StringUtil.isEmpty(content.getContentType())) {
			if (upperContent != null) {
				content.setContentType(upperContent.getContentType());
			} else {
				content.setContentType(ContentType.FEED.getCode());
			}
		}

		if (StringUtil.isEmpty(content.getActive())) {
			content.setActive(ContentActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(content.getStatus())) {
			content.setStatus(ContentStatus.REG.getCode());
		}

		content.setBasicInfo(dateStr, userId);

		int depth = upperDepth + 1;
		String path = StringUtil.getPath(seq, upperPath, depth);

		content.setPath(path);
		content.setDepth(depth);
		save(content);

		Count count = new Count();
		count.setContentId(contentId);
		count.setFavCnt(0);
		count.setLikeCnt(0);
		count.setViewCnt(0);
		save(count);
		view.setCount(count);

		Price price = view.getPrice();
		String priceId = null;
		if (price != null) {
			priceId = CommonUtil.getGuid();
			price.setPriceId(priceId);
			price.setPriceType(PriceType.PRODUCT.getCode());
			price.setBasicInfo(dateStr, userId);
			save(price);
			view.setPrice(price);
		}

		List<Resources> hashList = view.getHashList();
		if (hashList != null && !hashList.isEmpty()) {
			int order = 0;
			String hashId;
			for (Resources hash : hashList) {
				hashId = CommonUtil.getGuid();
				hash.setResourceId(hashId);
				hash.setContentId(contentId);
				hash.setResourceType(ResourceType.HASHTAG.getCode());
				hash.setOrders(++order);
				hash.setBasicInfo(dateStr, userId);
				save(hash);
			}
		}

		List<Others> featureList = view.getFeatureList();
		if (!StringUtil.isEmpty(featureList)) {
			String otherId;
			for (Others others : featureList) {
				otherId = CommonUtil.getGuid();
				others.setContentId(contentId);
				others.setOtherId(otherId);
				others.setBasicInfo(dateStr, userId);
				save(others);
			}
		}

		return view;
	}

	public long getContentSeq() {
		long seq = contentMapper.getContentSeq();
		return seq;
	}

	public int checkDelProduct(Content content) {
		List<Resources> list = contentMapper.checkProductCompare(content);
		return list.size();
	} 
	
	public Content deleteContent(Content content, String userId) {
		String dateStr = CommonUtil.getSysTime();
		content = getContent(content.getContentId());
		content.setDelete();
		content.setBasicInfo(dateStr, userId);
		save(content);
		
		List<Resources> resourceList = resourceRepo.findByContentId(content.getContentId());
		List<Others> othersList = othersRepo.findByContentId(content.getContentId()); 
		List<WeatherProduct> weatherProductList = weatherProductRepo.findByRefId(content.getContentId());
		if(resourceList.size()>0) {
			for (Resources resources : resourceList) {
				resources.setDelete();
				save(resources);
			}
		}
		if(othersList.size()>0) {
			for (Others other : othersList) {
				other.setDelete();
				save(other);
			}
		}
		if(weatherProductList.size()>0) {
			for (WeatherProduct weatherPd : weatherProductList) {
				weatherPd.setDelete();
				save(weatherPd);
			}
		}
		
		
		return content;
	}

	public Resources deleteResource(String resourceId, String userId) {
		String dateStr = CommonUtil.getSysTime();
		Resources resource = getResource(resourceId);
		resource.setDelete();
		resource.setBasicInfo(dateStr, userId);
		save(resource);
		return resource;
	}

	public Content saveContent(Content content, String userId) {
		String dateStr = CommonUtil.getSysTime();

		if (StringUtil.isEmpty(content.getContentType())) {
			content.setContentType(ContentType.FEED.getCode());
		}

		if (StringUtil.isEmpty(content.getActive())) {
			content.setActive(ContentActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(content.getStatus())) {
			content.setStatus(ContentStatus.REG.getCode());
		}

		content.setBasicInfo(dateStr, userId);
		save(content);

		return content;
	}

	/**
	 * Content를 수정한다.
	 * 
	 * @param ContentView
	 * @return ContentView
	 */
	
	public ContentView saveContentView(ContentView view, String loginId) {
		// Content
		String dateStr = CommonUtil.getSysTime();
		Content content = view.getContent();
		String contentId = content.getContentId();

		if (StringUtil.isEmpty(content.getContentType())) {
			content.setContentType(ContentType.FEED.getCode());
		}

		if (StringUtil.isEmpty(content.getActive())) {
			content.setActive(ContentActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(content.getStatus())) {
			content.setStatus(ContentStatus.REG.getCode());
		}

		content.setBasicInfo(dateStr, loginId);
		save(content);

		// Price
		Price price = view.getPrice();
		String priceId = null;
		if (price != null) {
			price.setContentId(contentId);
			if (StringUtil.isEmpty(price.getPriceId())) {
				priceId = CommonUtil.getGuid();
				price.setPriceId(priceId);
			}
			price.setPriceType(PriceType.PRODUCT.getCode());
			price.setBasicInfo(dateStr, loginId);
			save(price);
			view.setPrice(price);
		}

		// Others 특장점
		List<Others> featureList = view.getFeatureList();
		if (!StringUtil.isEmpty(featureList)) {
			String otherId;
			for (Others others : featureList) {
				others.setContentId(contentId);

				otherId = others.getOtherId();
				if (StringUtil.isEmpty(otherId)) {
					otherId = CommonUtil.getGuid();
					others.setOtherId(otherId);
				}

				others.setBasicInfo(dateStr, loginId);
				save(others);
			}
		}

//		private List<Others> featureList; //특장점 
//		private List<Others> simiarList; // 유사품
//		private List<Others> productImgList; // 제품이미지 목록

		// Hash Tag ==> 현재 미사용
		List<Resources> hashList = view.getHashList();
		if (hashList != null && !hashList.isEmpty()) {
			int order = 0;
			String hashId = null;
			for (Resources hash : hashList) {
				if (StringUtil.isEmpty(hash.getResourceId())) {
					hashId = CommonUtil.getGuid();
					hash.setResourceId(hashId);
				}

				hash.setContentId(content.getContentId());
				hash.setResourceType(ResourceType.HASHTAG.getCode());
				hash.setOrders(order++);
				hash.setBasicInfo(dateStr, loginId);
				save(hash);
			}
		}

		return view;
	}

	/**
	 * 화면에서 조회할 때는 사용하는 함수
	 * 
	 * @param contentId
	 * @param userId
	 * @return
	 */
	public ContentView getContentView(String contentId, String userId) {
		// TODO userId 를 사용해서 조회 가능한지 권한 체크해야 함. ==> 공통 사항.
		Content content = contentRepo.findByContentId(contentId);
		if (content == null) {
			return null;
		}

		String ownerId = content.getOwnerId();
		if (userId.equals(ownerId)) {
			// 무조건 조회가능
		} else {
			String viewType = content.getViewType();
			if (ContentViewType.ALL.getCode().equals(viewType)) {
				// 성공
			} else if (ContentViewType.ONLYME.getCode().equals(viewType)) {
				// 오류
				log.error("====== 권한 오류 (자신만 보기가능) =========");
			} else if (ContentViewType.FOLLOW.getCode().equals(viewType)) {
				// userId로 팔로우 테이블 조회해서 체크
				boolean realFollow = userService.isRealFollow(userId, ownerId);
				if (!realFollow) {
					log.error("====== 권한 오류 (Follow만 가능한데 Follow 아님) =========");
				}
			} else if (ContentViewType.ADMIN.getCode().equals(viewType)) {
				// userId로 유저 권한 체크
				boolean isAdmin = userService.isAdmin(userId);
				if (!isAdmin) {
					log.error("====== 권한 오류 (Admin권한 필요) =========");
				}
			}
		}

		setViewCnt(contentId, userId);
		ContentView view = getContentViewByContent(content, userId);

		return view;
	}

	/**
	 * 내부에서만 사용하는 함수 ==> 화면에서 조회할 때는 위에 함수를 사용해야 함.
	 * 
	 * @param contentId
	 * @return
	 */
	public ContentView getContentView(String contentId) {
		Content content = contentRepo.findByContentId(contentId);
		if (content == null) {
			return null;
		}

		ContentView view = getContentViewByContent(content, null);
		return view;
	}

	public ContentView getContentViewAllResource(String contentId) {
		Content content = contentRepo.findByContentId(contentId);
		if (content == null) {
			return null;
		}

		ContentView view = getContentViewByContent(content, null, null, true);
		return view;
	}

	private Map<String, UserListView> getUserObjMap(List<Content> contentList) {
		List<String> userids = new ArrayList<String>();
		for (Content content : contentList) {
			userids.add(content.getOwnerId());
		}

		Map<String, UserListView> userObjMap = userService.getUserObjMap(userids);

		return userObjMap;
	}

	private ContentView getContentViewByContent(Content content, String searchUserid) {
		return getContentViewByContent(content, null, searchUserid);
	}

	private ContentView getContentViewByContent(Content content, Map<String, UserListView> userObjMap,
			String searchUserid) {
		return getContentViewByContent(content, userObjMap, searchUserid, false);
	}

	private ContentView getContentViewByContent(Content content, Map<String, UserListView> userObjMap,
			String searchUserid, boolean flag ) { 
		
		ContentView view = new ContentView();
		String contentId = content.getContentId();
		if (userObjMap == null) {
			User user = userService.getUser(content.getOwnerId());
			if (user != null) {
				content.setOwnerName(user.getUserName());
				String ownerImgUrl = userService.getUrl(user);
				content.setOwnerImgUrl(ownerImgUrl);
//				content.setOwnerImgData(getImageData(ownerImgUrl));//ImageData 주석
//				content.setOwnerImgData(getImageData(ownerImgUrl));

			}
		} else {
			UserListView userListView = userObjMap.get(content.getOwnerId());
			if (userListView == null) {
				content.setOwnerName("미등록자");
				content.setOwnerImgUrl(NO_USER_IMG_PATH);
			} else {
				content.setOwnerName(userListView.getUserName());
				content.setOwnerImgUrl(userListView.getImgUrl());
//				content.setOwnerImgData(getImageData(userListView.getImgUrl()));
			}
		}

		if (content.isMainImg()) {
			content.setMainUrl(getUrl(content));
//			content.setMainImgData(getImageData(getUrl(content)));//ImageData 주석
//			content.setMainImgData(getImageData(content.getMainUrl()));
		} else {
			content.setMainUrl(NO_CONTENT_IMG_PATH);
		}

		content.setEnumName();

		List<Resources> resourceList = new ArrayList<>();
		List<Resources> tempResourceList = resourceRepo.findByContentId(contentId,
				Sort.by(Sort.Direction.ASC, "orders"));

		for (Resources resources : tempResourceList) {
			if (!flag) { //flag 가 TRUE면 삭제된것도 포함시킴 
				if (resources.isDelete()) {
					continue;
				}
			}

			if (ResourceType.isStorageUrl(resources.getResourceType())) {
				resources.setResourceUrl(getUrl(resources));
				if(resources.getFilename()!=null&&!"".equals(resources.getFilename())&&resources.getResourceUrl()!=null&&"".equals(resources.getResourceUrl())) {
				resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),resources.getResourceUrl()));
//				resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),getUrl(resources)));
				}
				resources.setLink(resources.getUrl());
				
			} else if (ResourceType.isUTubeUrl(resources.getResourceType())) {
				resources.setResourceUrl("https://www.youtube.com/embed/" + resources.getResourceName()
						+ "?theme=dark&autoplay=1&autohide=0&cc_load_policy=1&mute=1&loop=1&controls=0&disablekb=1&rel=0&iv_load_policy=3&fs=0&playlist="
						+ resources.getResourceName() + "&modestbranding=1&showinfo=0");
				resources.setLink(resources.getUrl());
			} else if (ResourceType.isText(resources.getResourceType())) {
			} else if (ResourceType.isProduct(resources.getResourceType())) {

				try {
					long seq = Long.parseLong(resources.getResourceName());
					Optional<Content> productOpt = contentRepo.findById(seq);
					Content product = productOpt.get();
					resources.setResourceUrl(getUrl(product));
					if(resources.getFilename()!=null&&"".equals(resources.getFilename())) {
//					resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),resources.getResourceUrl()));
					resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),getUrl(product)));
					}
					resources.setLink(product.getContentId());
					resources.setResourceTitle(product.getTitle()); // 제품명
					resources.setResourceName(product.getVendor()); // Vendor
				} catch (NumberFormatException e) {
					resources.setResourceUrl(NO_CONTENT_IMG_PATH);
					resources.setLink("");
				}

			}
			resourceList.add(resources);
		}
		List<Likes> likeList = null;
		if (searchUserid != null) {
			Likes like = new Likes();
			like.setContentId(contentId);
			like.setOwnerId(searchUserid);
			like.setLikeType(LikeType.LIKE.getCode());
			likeList = contentMapper.getLikeByUserId(like);
		}

		if (StringUtil.isEmpty(likeList)) {
			likeList = new ArrayList<Likes>();
		} else {
			if (likeList.size() > 1) {
				Likes like = likeList.get(0);
				likeList = new ArrayList<Likes>();
				likeList.add(like);
			}
		}

		view.setLikeList(likeList);

		ContentCondition condition = new ContentCondition();
		String relpyPath = StringUtil.getRelpyPath(content.getSeq());
		condition.setPath(relpyPath);

		int replyCnt = contentMapper.getReplyListCnt(condition);

		Count count = countRepo.findByContentId(contentId);
		if (count == null) {
			count = new Count();
		}
		count.setReplyCnt(replyCnt);

		Price price = null;
		if (content.isProduct()) { // 제품 관련 처리 로직
			price = priceRepo.findByContentId(contentId);
			List<Others> others = getOthers(contentId);

			setImgUrlAtOthers(others);

			view.setOthersData(others);
		} else {
			price = new Price();
		}

		if (content.isProduct()) { // Category 명 셋팅
			content.setCategory1Name(getClass02Name(content.getCategory1()));
			content.setCategory2Name(getClass03Name(content.getCategory2()));

			// 평균 평점 셋팅
			float avgReviewGrade = getAvgReviewGrade(content);
			content.setAvgReviewGrade(avgReviewGrade);

		}

		view.setCount(count);
		view.setPrice(price);
		view.setContent(content);
		view.setResourceList(resourceList);

		Follow follow = userService.getFollowByUserId(searchUserid, content.getOwnerId());
		if (follow != null) {
			follow.removeResInfo(); // 상대방 블락 처리에 대해서 정보를 제거한다.
			view.setFollow(follow);
		}

		return view;
	}


	private void setImgUrlAtOthers(List<Others> others) {
		for (Others other : others) {
			other.setImgUrl(getUrl(other));

			if (OtherType.FEATURE.getCode().equals(other.getOtherType()) || StringUtil.isEmpty(other.getOtherName())) { // 웹
																														// 이미지
																														// 링크일때만
			} else {
				other.setFileInfo(getFileInfo(FileInfoType.OTHERS.getCode(),other.getOtherName(),other.getImgUrl()));
			}

		}
	}
	private float getAvgReviewGrade(Content content) {
		ContentCondition condition = new ContentCondition();

		String relpyPath = StringUtil.getRelpyPath(content.getSeq());
		condition.setPath(relpyPath);
		Float avgReviewGrade = contentMapper.getAvgReviewGrade(condition);

		if (avgReviewGrade == null)
			return 0;
		avgReviewGrade = MathUtil.roundScore(avgReviewGrade);
		return avgReviewGrade;
	}

	public Resources saveResource(MultipartFile file, Resources inputResource, String userId) throws IOException {
		String dateStr = CommonUtil.getSysTime();
		Resources oldResource = getResource(inputResource.getResourceId());
		Resources neoResource = null;

		if (FileUtil.isEmpty(file)) { // 신규 파일이 없다.
			// 이미지/동영상 ==> 유트뷰
			if (ResourceType.isStorageUrl(oldResource.getResourceType())) {
				if (ResourceType.isStorageUrl(inputResource.getResourceType())) { // 파일 ==> 파일
					// 문구/Link 만 변경 가능함.
					oldResource.setResourceContent(inputResource.getResourceContent());
					if (!StringUtil.isEmpty(inputResource.getOrders())) {
						oldResource.setOrders(inputResource.getOrders());
					}

					oldResource.setResourceTitle(inputResource.getResourceTitle());
					oldResource.setUrl(inputResource.getUrl());
					oldResource.setBasicInfo(dateStr, userId);
					if(inputResource.getResourceCategory()!=null && !"".equals(inputResource.getResourceCategory())){
						oldResource.setResourceCategory(inputResource.getResourceCategory());
					}
					save(oldResource);
					neoResource = oldResource;
				} else { // 파일 ==> URL
					oldResource.setDelete();
					oldResource.setBasicInfo(dateStr, userId);
					save(oldResource);

					neoResource = getResources(file, userId, inputResource.getContentId(),
							inputResource.getResourceTitle(), inputResource.getResourceContent(),
							inputResource.getUrl(),inputResource.getResourceCategory());

					if (!StringUtil.isEmpty(inputResource.getOrders())) {
						oldResource.setOrders(inputResource.getOrders());
					} else {
						neoResource.setOrders(oldResource.getOrders());
					}

					neoResource = createFile(file, userId, neoResource);
				}
			} else { // 유튜브 ==> 유튜브
						// 데이터만 업데이트 ==> order 유지
				if (!StringUtil.isEmpty(inputResource.getOrders())) {
					oldResource.setOrders(inputResource.getOrders());
				}
				if (ResourceType.isProduct(inputResource.getResourceType())) {
					Content ct = contentRepo.findByContentId(inputResource.getUrl());
					if(ct==null) {
						long searchContentSeq = Long.parseLong(inputResource.getUrl());
						ct = contentRepo.findById(searchContentSeq).get();
						oldResource.setResourceName("" + searchContentSeq);
					}else {
						oldResource.setResourceName("" + ct.getSeq());
					}
					try {
						ContentView view = getContentViewByContent(ct, null);
						ct = view.getContent();
						oldResource.setResourceTitle(ct.getTitle());
						oldResource.setResourceContent(ct.getContent());
						if(ct.getMainFilename()!=null&&!"".equals(ct.getMainFilename())) {
							oldResource.setFilename(ct.getMainFilename());
						}
						if(ct.getMainDir()!=null && !"".equals(ct.getMainDir())) {
							oldResource.setDir(ct.getMainDir());
						}
						oldResource.setUrl(ct.getContentId());
						save(oldResource);
						neoResource = oldResource;
						return neoResource;
					} catch (NoSuchElementException e) {
						return null;
					} catch (Exception e) {
						return null;
					}

				}
				oldResource.setResourceContent(inputResource.getResourceContent());
				oldResource.setResourceTitle(inputResource.getResourceTitle());
				if(inputResource.getResourceCategory()!=null && !"".equals(inputResource.getResourceCategory())){
					oldResource.setResourceCategory(inputResource.getResourceCategory());
				}
				oldResource.setUrl(inputResource.getUrl()); // 수정용
				oldResource.setResourceName(FileUtil.getYoutubeId(inputResource.getUrl())); // Utube id
				oldResource.setBasicInfo(dateStr, userId);
				save(oldResource);
				neoResource = oldResource;
			}
		} else { // 신규 파일이 있다, 파일도 변경해야 함.
			// 신규 데이터 추가
			neoResource = getResources(file, userId, inputResource.getContentId(), inputResource.getResourceTitle(),
					inputResource.getResourceContent(), inputResource.getUrl(),inputResource.getResourceCategory());
			if (!StringUtil.isEmpty(inputResource.getOrders())) {
				neoResource.setOrders(inputResource.getOrders());
			} else {
				neoResource.setOrders(oldResource.getOrders()); // ==> order 유지
			}
			createFile(file, userId, neoResource);
			getFileInfo(FileInfoType.RESOUCES.getCode(), neoResource.getFilename(), getUrl(neoResource));

			// 기존 데이터 삭제 처리
			oldResource.setDelete();
			oldResource.setBasicInfo(dateStr, userId);
			save(oldResource);
		}
		
		return neoResource;
	}

	public Resources addResource(MultipartFile file, Resources inputResource, String userId) throws IOException {
		long seq = inputResource.getSeq();
		Resources neoResource = null;
		if (seq == 0) {// 신규 ==> seq 여부
			neoResource = getResources(file, userId, inputResource.getContentId(), inputResource.getResourceTitle(),
					inputResource.getResourceContent(), inputResource.getUrl(),inputResource.getResourceType(),inputResource.getResourceCategory());
			if (neoResource == null) {
				return null;
			}
			int lastOrder = contentMapper.getLastOrder(inputResource.getContentId());
			if (inputResource.getOrders() != 0) {
				neoResource.setOrders(inputResource.getOrders());
			} else {
				neoResource.setOrders(++lastOrder);
			}
			createFile(file, userId, neoResource);
			if(neoResource.getFilename()!=null&&!"".equals(neoResource.getFilename())) {
				getFileInfo(FileInfoType.RESOUCES.getCode(), neoResource.getFilename(), getUrl(neoResource),userId);
			}
		}

		return neoResource;
	}

	public Resources getResources(MultipartFile file, String userId, String contentId, String title, String content,
			String url) {
		return getResources(file, userId, contentId, title, content, url, null);
	}
	public Resources getResources(MultipartFile file, String userId, String contentId, String title, String content,
			String url, String resourceType) {
		return getResources(file, userId, contentId, title, content, url,null,null);
	}
	public Resources getResources(MultipartFile file, String userId, String contentId, String title, String content,
			String url, String resourceType,String resourceCategory) {
		Resources resource = new Resources();
		resource.setContentId(contentId);
		String resourceId = CommonUtil.getGuid();
		resource.setResourceId(resourceId);
		resource.setResourceTitle(title);
		resource.setResourceContent(content);
		if(resourceCategory!=null && !"".equals(resourceCategory)) {
			resource.setResourceCategory(resourceCategory);
		}		
		
		if (resourceType != null) {
			resource.setResourceType(resourceType);
		} else {
			if (FileUtil.isEmpty(file)) {
				if (StringUtil.isUrl(url)) {
					resource.setResourceType(ResourceType.UTUBE.getCode());
				} else {
					resource.setResourceType(ResourceType.PRODUCT.getCode());
				}
			} else {
				resource.setResourceType(ResourceType.getCode(file).getCode());
			}
		}

		if (ResourceType.isStorageUrl(resource.getResourceType())) { // 이미지 / 동영상
			String uploadFileName = file.getOriginalFilename();
			resource.setResourceName(uploadFileName);

			String dir = bucket;
			resource.setDir(dir);

			String extention = FilenameUtils.getExtension(uploadFileName).toLowerCase();
			if ("JPEG".equalsIgnoreCase(extention)) {
				extention = "JPG";
			}
			StringBuffer sb = new StringBuffer();
			sb.append(contentFolder);
			sb.append("/").append(userId).append("/").append(resourceId).append(".").append(extention);
			String fileName = sb.toString();

			resource.setFilename(fileName);
		} else if (ResourceType.isUTubeUrl(resource.getResourceType())) { // 이미지 / 동영상
			resource.setResourceName(FileUtil.getYoutubeId(url)); // Utube id
		} else if (ResourceType.isText(resource.getResourceType())) {
			
		} else if (ResourceType.isProduct(resource.getResourceType())) { // 제품
			long searchContentSeq = Long.parseLong(url);
			if (searchContentSeq > 0) {
				try {
					Content ct = contentRepo.findById(searchContentSeq).get();
					ContentView view = getContentViewByContent(ct, null);
					ct = view.getContent();
					resource.setResourceTitle(ct.getTitle());
					resource.setResourceContent(ct.getContent());
					resource.setResourceName("" + searchContentSeq);
					if(ct.getMainFilename()!=null&&!"".equals(ct.getMainFilename())) {
						resource.setFilename(ct.getMainFilename());
					}
					if(ct.getMainDir()!=null&&!"".equals(ct.getMainDir())) {
						resource.setDir(ct.getMainDir());
					}
					url = ct.getContentId();

				} catch (NoSuchElementException e) {
					return null;
				}
			}

		}

		resource.setUrl(url); // 저장용

		return resource;
	}

	public List<Resources> createFiles(List<MultipartFile> files, String userId, String contentId, List<String> title,
			List<String> content, List<String> url) throws IOException {
		List<Resources> list = new ArrayList<>();
		Resources resource = null;
		int order = 0;
		int i = 0;
		for (MultipartFile file : files) {
			resource = getResources(file, userId, contentId, title.get(i), content.get(i), url.get(i));
			resource.setOrders(++order);
			list.add(resource);
			i++;
		}

		list = createFile(files, userId, list);

		return list;
	}

	public Content createContentFile(MultipartFile file, String userId, Content content) throws IOException {
		String mainFilename = saveImgFile(file, userId);

		String dir = bucket;
		content.setMainDir(dir);
		content.setMainFilename(mainFilename);

		uploadSerivce.saveUserImg(file, mainFilename);

		return content;
	}

	/**
	 * 이미지 저장 함수 ==> Content 대표 이미지 / 상품 이미지 등록에 사용
	 * 
	 * @param file
	 * @param userId
	 * @return
	 */
	private String saveImgFile(MultipartFile file, String userId) {
		String fileName = file.getOriginalFilename();

//		String imgId = content.getContentId();
		String imgId = getContentId(); // 신규로 발생하게함.
		String neoFileName = FileUtil.getNeoFileName(imgId, fileName); // 확장자는 여기서 붙
		String mainFilename = FileUtil.getStoragePath(contentFolder, userId, neoFileName, StoragePath.CONTENT_USER);
		return mainFilename;
	}

	public List<Others> modifiyProductImgFile(MultipartFile prodcutImgFile, String userId, String contentId,
			String otherId) throws IOException {
		Others other = getOthers(contentId, otherId);
		String mainFilename = saveImgFile(prodcutImgFile, userId);
		uploadSerivce.saveUserImg(prodcutImgFile, mainFilename);

		// other.setOtherCode(bucket);
		other.setOtherName(mainFilename);
		other.setOtherValue(bucket);

		other.setBasicInfo(userId);
		save(other);

		List<Others> oldOthers = getOthers(contentId, OtherType.PRODUCT_IMG);
		setImgUrlAtOthers(oldOthers);

		return oldOthers;
	}

	public List<Others> deleteProductImgFile(String userId, String contentId, String otherId) throws IOException {
		Others other = getOthers(contentId, otherId);
		other.setOtherStatus(OtherStatus.DEL.getCode());
		other.setBasicInfo(userId);
		save(other);

		List<Others> oldOthers = getOthers(contentId, OtherType.PRODUCT_IMG);
		setImgUrlAtOthers(oldOthers);
		return oldOthers;
	}

	/**
	 * 유사 상품 추가
	 * 
	 * @param userId
	 * @param contentId
	 * @param productIds
	 * @return
	 */
	public List<Others> createPeerProduct(String contentId, List<String> productIds, String userId) {

		List<Others> peerProductList = new ArrayList<>();
		Others peerProduct = null;
		for (String productId : productIds) {
			peerProduct = new Others();
			peerProduct.setOtherCode(productId);
			peerProductList.add(peerProduct);
		}

		// TODO createProuctImg ==> 최적화 해야 함.
		List<Others> others = createProuctImg(userId, contentId, peerProductList, OtherType.PEER_PRODUCT);
		others = getOthers(contentId, OtherType.PEER_PRODUCT);
		setImgUrlAtOthers(others);
		Collections.sort(others);
		return others;
	}

	public List<Others> modifyPeerProduct(String contentId, String otherId, String productId, String userId) {

		Others peerProduct = getOthers(contentId, otherId);
		peerProduct.setOtherCode(productId);
		peerProduct.setBasicInfo(userId);
		save(peerProduct);
		List<Others> others = getOthers(contentId, OtherType.PEER_PRODUCT);
		setImgUrlAtOthers(others);
		return others;
	}

	public List<Others> deletePeerProduct(String contentId, String otherId, String userId) {

		Others peerProduct = getOthers(contentId, otherId);
		peerProduct.setOtherStatus(OtherStatus.DEL.getCode());
		peerProduct.setBasicInfo(userId);
		save(peerProduct);
		List<Others> others = getOthers(contentId, OtherType.PEER_PRODUCT);
		setImgUrlAtOthers(others);
		return others;
	}

	/**
	 * 제품 이미지 추가
	 * 
	 * @param prodcutImgFiles
	 * @param userId
	 * @param contentId
	 * @return
	 * @throws IOException
	 */
	public List<Others> createProductImgFile(List<MultipartFile> prodcutImgFiles, String userId, String contentId)
			throws IOException {

		List<Others> productImgList = new ArrayList<>();
		Others productImg = null;
		// 파일 저장
		String mainFilename = null;
		for (MultipartFile file : prodcutImgFiles) {
			mainFilename = saveImgFile(file, userId);
			uploadSerivce.saveUserImg(file, mainFilename);

			productImg = new Others();
			// productImg.setOtherCode(bucket);
			productImg.setOtherName(mainFilename);
			productImg.setOtherValue(bucket);
			productImgList.add(productImg);
		}

		List<Others> oldOthers = createProuctImg(userId, contentId, productImgList, OtherType.PRODUCT_IMG);

		setImgUrlAtOthers(oldOthers);
		return oldOthers;
	}

	private List<Others> createProuctImg(String userId, String contentId, List<Others> productImgList,
			OtherType otherType) {
		List<Others> oldOthers = getOthers(contentId, otherType);
		int otherOrder = 0;
		if (StringUtil.isEmpty(oldOthers)) {
			oldOthers = new ArrayList<>();
		} else {
			Collections.sort(oldOthers);
			int size = oldOthers.size();
			Others lastOthers = oldOthers.get(size - 1);
			otherOrder = lastOthers.getOtherOrder();
		}

		// db 저장
		String dateStr = CommonUtil.getSysTime();
		String otherId;
		for (Others other : productImgList) {
			otherOrder++;
			other.setContentId(contentId);
			otherId = other.getOtherId();
			if (StringUtil.isEmpty(otherId)) {
				otherId = CommonUtil.getGuid();
				other.setOtherId(otherId);
			}
			other.setOtherType(otherType.getCode());
			other.setOtherStatus(OtherStatus.REG.getCode());
			other.setOtherOrder(otherOrder);

			other.setBasicInfo(dateStr, userId);
			save(other);

			oldOthers.add(other);
		}
		return oldOthers;
	}

	public List<Resources> createFile(List<MultipartFile> files, String userId, List<Resources> resources)
			throws IOException {

		int i = 0;
		Resources resource = null;
		for (MultipartFile file : files) {
			resource = resources.get(i++);
			resource = createFile(file, userId, resource);
		}
		return resources;
	}

	public Resources createFile(MultipartFile file, String userId, String contentId, String title, String content,
			String url) throws IOException {
		Resources resource = getResources(file, userId, contentId, title, content, url);

		return createFile(file, userId, resource);
	}

	public Resources createFile(MultipartFile file, String userId, Resources resource) throws IOException {
		if (resource.getOrders() == 0) {
			resource.setOrders(1);
		}

		if (resource.isStorage()) {
			uploadSerivce.saveUserImg(file, resource.getFilename());
		}

//파일을 먼저 저장한 이후에 테이블에 저장한다. 		
		String dateStr = CommonUtil.getSysTime();
		resource.setBasicInfo(dateStr, userId);
		save(resource);

		return resource;
	}

	public Likes saveLikes(Likes like) {
//		LIKES	seq ==> 있냐 없냐에 따라 다름
		if (StringUtil.isEmpty(like.getLikeId())) {
			like.setLikeId(CommonUtil.getGuid());
		}

		if (StringUtil.isEmpty(like.getContentId())) {
			like.setError(ErrMsg.CONTENT_NO_ID);
			return like;
		}

		if (StringUtil.isEmpty(like.getOwnerId())) {
			like.setError(ErrMsg.NO_USER_ID);
			return like;
		}

		String dateStr = CommonUtil.getSysTime();
		like.setBasicInfo(dateStr, like.getOwnerId());

		save(like);

		int count = setCount(like.getContentId(), like.getLikeType(), Boolean.TRUE);
		like.setLikeCnt(count);

		return like;
	}

	public Likes deleteFlagLikes(Likes like) {

//		if(StringUtil.isEmpty(like.getContentId())) {
//			like.setError(ErrMsg.CONTENT_NO_ID);
//			return like;
//		}
//		
//		if(StringUtil.isEmpty(like.getOwnerId())) {
//			like.setError(ErrMsg.NO_USER_ID);
//			return like;
//		}
//		
//		String likeId = like.getLikeId();
//		if(StringUtil.isEmpty(likeId)) {
//			likeId = contentMapper.getLikeId(like);			
//		}

		String likeId = like.getLikeId();
		if (StringUtil.isEmpty(likeId)) {
			like.setError(ErrMsg.NO_RESULT);
			return like;
		}

		Likes dbLikes = likeRepo.findByLikeId(likeId);

		if (dbLikes == null) {
			like.setError(ErrMsg.LIKE_DELETE_ERR);
			return like;
		}

//		if( ! dbLikes.getOwnerId().equals(like.getOwnerId())) {
//			like.setError(ErrMsg.LIKE_DELETE_ERR);
//			return like;
//		}

		String dateStr = CommonUtil.getSysTime();
		dbLikes.setBasicInfo(dateStr, like.getOwnerId());

		dbLikes.setDelete();
		save(dbLikes);

		int count = setCount(dbLikes.getContentId(), dbLikes.getLikeType(), Boolean.FALSE);
		like.setLikeCnt(count);

		return like;
	}

	/**
	 * TODO 케쉬 대상
	 * 
	 * @param categoryId
	 * @return
	 */
	private String getClass02Name(String categoryId) {
		String name = contentMapper.getClass02Name(categoryId);
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * TODO 케쉬 대상
	 * 
	 * @param categoryId
	 * @return
	 */
	private String getClass03Name(String categoryId) {
		String name = contentMapper.getClass03Name(categoryId);
		if (name == null)
			name = "";
		return name;
	}

	private Likes deleteLikes(Likes like) {
		likeRepo.deleteById(like.getSeq());
		return like;
	}

	public int setCount(String contentId, String likeType, boolean isPlus) {
		LikeType likeTypeEnum = LikeType.FAV.getEnum(likeType);
		return setCount(contentId, likeTypeEnum, isPlus);
	}

	private void setViewCnt(String contentId, String userId) {
		Likes like = new Likes();
		like.setContentId(contentId);
		like.setOwnerId(userId);
		like.setLikeType(LikeType.VIEW.getCode());
		saveLikes(like);	// viwe count 같이 올라감.
	}
	
	public int setCount(String contentId, LikeType likeType, boolean isPlus) {
		Count count = new Count();
		count.setContentId(contentId);
		
		switch (likeType) {
		case VIEW:
			if (isPlus) {
				contentMapper.plusViewCount(count);
			} else {
				contentMapper.minusViewCount(count);
			}
			break;

		case FAV:
			if (isPlus) {
				contentMapper.plusFavCount(count);
			} else {
				contentMapper.minusFavCount(count);
			}
			break;

		case LIKE:
			if (isPlus) {
				contentMapper.plusLikeCount(count);
			} else {
				contentMapper.minusLikeCount(count);
			}
			break;

		case REPLY:
			if (isPlus) {
				contentMapper.plusReplyCount(count);
			} else {
				contentMapper.minusReplyCount(count);
			}
			break;

		default:
			break;
		}

		Count neoCount = countRepo.findByContentId(contentId);
		if (neoCount == null) {
			return 0;
		}

		int result = 0;
		switch (likeType) {
		case VIEW:
			result = neoCount.getViewCnt();
			break;

		case FAV:
			result = neoCount.getFavCnt();

			break;

		case LIKE:
			result = neoCount.getLikeCnt();
			break;

		case REPLY:
			result = neoCount.getReplyCnt();
			break;

		default:
			break;
		}

		return result;
	}

	/**
	 * 처리 전용 (조회에 사용하면 안됨)
	 * 
	 * @param contentId
	 * @param otherId
	 * @return
	 */
	private Others getOthers(String contentId, String otherId) {
		return othersRepo.findByContentIdAndOtherId(contentId, otherId);
	}

	private List<Others> getOthers(String contentId, OtherType otherType) {
		String otherstatus = OtherStatus.REG.getCode();
		List<Others> results = othersRepo.findByContentIdAndOtherTypeAndOtherStatus(contentId, otherType.getCode(),
				otherstatus);

		if (OtherType.PEER_PRODUCT.equals(otherType)) {
			setPeerProductInfo(results);
		}

		return results;
	}

	private void setPeerProductInfo(List<Others> results) {
		Map<String, Others> peerProductInfoMap = getPeerProductInfoMapByOthers(results);
		String productId = null;
		Others product = null;
		for (Others other : results) {
			productId = other.getOtherCode();
			product = peerProductInfoMap.get(productId);

			other.setTitle(product.getTitle());
			other.setOtherName(product.getOtherName());
			other.setOtherValue(product.getOtherValue());

			other.setCurr(product.getCurr());
			other.setPrice(product.getPrice());
			other.setPriceUrl(product.getPriceUrl());
		}
	}

	private List<Others> getOthers(String contentId) {
		String otherStatus = OtherStatus.REG.getCode();
		List<Others> results = othersRepo.findByContentIdAndOtherStatus(contentId, otherStatus);

		List<Others> peerProduct = new ArrayList<Others>();
		for (Others others : results) {
			if (OtherType.PEER_PRODUCT.getCode().equals(others.getOtherType())) {
				peerProduct.add(others);
			}
		}

		setPeerProductInfo(peerProduct);

		return results;
	}

	public void save(Others other) {
		othersRepo.save(other);
	}
	
	public void save(WeatherProduct weatherProduct) {
		weatherProductRepo.save(weatherProduct);
	}

	public void save(Content content) {
		contentRepo.save(content);
	}

	public void save(Resources resource) {
		resourceRepo.save(resource);
	}

	public void save(Likes like) {
		likeRepo.save(like);
	}

	public void save(Price price) {
		priceRepo.save(price);
	}

	public void save(Count count) {
		countRepo.save(count);
	}

	private String getUrl(Others other) {
		if (OtherType.FEATURE.getCode().equals(other.getOtherType())) {
			return "";
		}

		if (StringUtil.isEmpty(other.getOtherName())) {
			return NO_CONTENT_IMG_PATH;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(other.getOtherValue()).append("/").append(other.getOtherName());
		return sb.toString();

	}

	private String getUrl(Resources resource) {
		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(resource.getDir()).append("/").append(resource.getFilename());
		return sb.toString();

	}

	public String getUrl(Content content) {
		if (StringUtil.isEmpty(content.getMainDir())) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(content.getMainDir()).append("/").append(content.getMainFilename());
		return sb.toString();

	}

	@Deprecated
	private void saveResource(MultipartFile file, String userId, Resources resource) throws IOException {

		String uploadFileName = file.getOriginalFilename();
		String extention = FilenameUtils.getExtension(uploadFileName).toLowerCase();
		if ("JPEG".equalsIgnoreCase(extention)) {
			extention = "JPG";
		}

		String resourceId = resource.getResourceId();
		if (StringUtil.isEmpty(resourceId)) {
			resourceId = CommonUtil.getGuid();
		}

		String dir = bucket;

		StringBuffer sb = new StringBuffer();
		sb.append(contentFolder);
		sb.append("/").append(userId).append("/").append(resourceId).append(".").append(extention);
		String fileName = sb.toString();

		if (uploadFileName.length() > 180) {
			// TODO file 명 길이가 길어질 경우 처리 로직 추가
//			uploadFileName = uploadFileName.substring(beginIndex, endIndex);
		}

		resource.setResourceId(resourceId);
		resource.setResourceName(uploadFileName);
		resource.setDir(dir);
		resource.setFilename(fileName);

		uploadSerivce.saveUserImg(file, fileName);
	}

	public List<Content> searchProductName(String productName) {
		return contentMapper.searchProductName(productName);
	}

	public List<AiRecommandProduct> AiRecommandProduct() {
		List<AiRecommandProduct> list = new ArrayList<>();
		AiRecommandProduct pd;
		List<Resources> productList;
		List<Content> contentList = contentRepo.findByContentType(ContentType.AI_PRODUCT.getCode());
		for (Content content : contentList) {
			pd = new AiRecommandProduct();
			pd.setContentId(content.getContentId());
			pd.setTitle(content.getTitle());
			productList = new ArrayList<>();
			List<Resources> tempResourceList = resourceRepo.findByContentId(pd.getContentId(),
					Sort.by(Sort.Direction.ASC, "orders"));
			for (Resources resources : tempResourceList) {
				if (resources.isDelete()) {
					continue;
				}

				if (ResourceType.isStorageUrl(resources.getResourceType())) {
					resources.setResourceUrl(getUrl(resources));
					resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),resources.getResourceUrl()));
//					resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),getUrl(resources)));
					resources.setLink(resources.getUrl());
				} else if (ResourceType.isUTubeUrl(resources.getResourceType())) {
					resources.setResourceUrl("https://www.youtube.com/embed/" + resources.getResourceName()
							+ "?theme=dark&autoplay=1&autohide=0&cc_load_policy=1&mute=1&loop=1&controls=0&disablekb=1&rel=0&iv_load_policy=3&fs=0&playlist="
							+ resources.getResourceName() + "&modestbranding=1&showinfo=0");
					resources.setLink(resources.getUrl());
				} else if (ResourceType.isProduct(resources.getResourceType())) {
					try {
						long seq = Long.parseLong(resources.getResourceName());
						Optional<Content> productOpt = contentRepo.findById(seq);
						Content product = productOpt.get();
						resources.setResourceUrl(getUrl(product));
						resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),resources.getResourceUrl()));
//						resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),getUrl(product)));
						resources.setLink(product.getContentId());
						resources.setResourceTitle(product.getTitle()); // 제품명
						resources.setResourceName(product.getVendor()); // Vendor
					} catch (NumberFormatException e) {
						resources.setResourceUrl(NO_CONTENT_IMG_PATH);
						resources.setLink("");
					}

				}
				productList.add(resources);
			}
			pd.setProductList(productList);
			pd.setProductSizeOnRandom(3);
			list.add(pd);
		}
		return list;
	}

	private List<Integer> getRandom(int size, int max) {
		Set<Integer> set = new HashSet<>();
		while (set.size() < size) {
			int random = (int) (Math.random() * max);
			set.add(random);
		}
		List<Integer> list = new ArrayList<>(set);
		Collections.sort(list);

		return list;
	}
	
	private ContentView getContentViewByContentNotAnother(Content content, Map<String, UserListView> userObjMap,
			String searchUserid ) {  // 관리자페이지 컨텐츠리스트 띄우는데 다른거 가져오는것 때매 너무 오래걸려서 하나빼놨음
		
		ContentView view = new ContentView();
		String contentId = content.getContentId();
		if (userObjMap == null) {
			User user = userService.getUser(content.getOwnerId());
			if (user != null) {
				content.setOwnerName(user.getUserName());
				String ownerImgUrl = userService.getUrl(user);
				content.setOwnerImgUrl(ownerImgUrl);

			}
		} else {
			UserListView userListView = userObjMap.get(content.getOwnerId());
			if (userListView == null) {
				content.setOwnerName("미등록자");
				content.setOwnerImgUrl(NO_USER_IMG_PATH);
			} else {
				content.setOwnerName(userListView.getUserName());
				content.setOwnerImgUrl(userListView.getImgUrl());
			}
		}

		if (content.isMainImg()) {
			content.setMainUrl(getUrl(content));
		} else {
			content.setMainUrl(NO_CONTENT_IMG_PATH);
		}

		content.setEnumName();

		ContentCondition condition = new ContentCondition();
		String relpyPath = StringUtil.getRelpyPath(content.getSeq());
		condition.setPath(relpyPath);

		int replyCnt = contentMapper.getReplyListCnt(condition);
		Count count = countRepo.findByContentId(contentId);
		if (count == null) {
			count = new Count();
		}
		count.setReplyCnt(replyCnt);

		if (content.isProduct()) { // Category 명 셋팅
			content.setCategory1Name(getClass02Name(content.getCategory1()));
			content.setCategory2Name(getClass03Name(content.getCategory2()));

			// 평균 평점 셋팅
			float avgReviewGrade = getAvgReviewGrade(content);
			content.setAvgReviewGrade(avgReviewGrade);

		}

		view.setCount(count);
		view.setContent(content);

		

		return view;
	}

	public List<ContentView> getNoticeAll() {
		List<ContentView> viewList =  new ArrayList<ContentView>();
		ContentView contentView;
		try {
			List<Content> noticeList = contentMapper.getNoticeAll();
			if (noticeList.size() > 0) {
				for (Content notice: noticeList) {
					contentView = getContentView(notice.getContentId());
					if(contentView != null ) {
						viewList.add(contentView);
					}
				}
				return viewList;
			}
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
		}
		return viewList;
	}
	
	public List<ContentView> getNotice(String noticeType) {
		List<ContentView> viewList =  new ArrayList<ContentView>();
		ContentView contentView;
		try {
			List<Content> noticeList = contentMapper.getNoticeByNoticeType(noticeType);
			if (noticeList.size() > 0) {
				for (Content notice: noticeList) {
					contentView = getContentView(notice.getContentId());
					if(contentView != null ) {
						viewList.add(contentView);
					}
				}
				return viewList;
			}
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
		}
		return viewList;
	}

	public List<ContentView> getNotice(String[] noticeType) {
		List<ContentView> viewList = new ArrayList<ContentView>();
		ContentView contentView;
		try {
			for (String string : noticeType) {
				List<Content> noticeList = contentMapper.getNoticeByNoticeType(string);
				if (noticeList.size() > 0) {
					for (Content notice : noticeList) {
						contentView = getContentView(notice.getContentId());
						if (contentView != null) {
							viewList.add(contentView);
						}
					}
				}
			}
			return viewList;
		} catch (Exception e) {
			log.error("AI PROCESS ERROR", e);
		}
		return viewList;
	}
	
	
	
	/* FILE INFO MIG */
	
	public void insertAmujit () {
		
	}
	
	public void insertFileInfo() {
		List<Resources> resourcesList = contentMapper.resourcesFile();
		int count = -1;
		for (Resources re : resourcesList) {
			count++;
		FileInfo fileInfo = fileInfoRepo.findByFilename(re.getFilename());
			if(fileInfo==null) {
			fileInfo = new FileInfo();
//			fileInfo.setBasicInfo("737c1cfdcf9245aab07d792856efd03e"); // 개발계 박준혁
			fileInfo.setBasicInfo("cab4676d20ff4da38bc65ce38e531c2b"); // 운영계 박준혁
			fileInfo.setFilename(re.getFilename());
			fileInfo.setType(FileInfoType.RESOUCES.getCode());
			String array[] = re.getFilename().split("[.]");
			if(array[array.length - 1].length()<10)
			fileInfo.setExtension(array[array.length - 1]);
			try {
				URL url = new URL(getUrl(re));
				Image image = ImageIO.read(url);
				fileInfo.setWidth(image.getWidth(null));
				fileInfo.setHeight(image.getHeight(null));
			} catch (Exception e) {
				// 이미지가아님
			}
			fileInfoRepo.save(fileInfo);
			}
		}
	}	
	
	public void insertFileInfo2() {
		List<Others> othersList = contentMapper.othersFile();
		int count = -1;
		for (Others other : othersList) {
			count++;
			FileInfo fileInfo = fileInfoRepo.findByFilename(other.getOtherName());
			if (fileInfo == null) {
				fileInfo = new FileInfo();
//				fileInfo.setBasicInfo("737c1cfdcf9245aab07d792856efd03e"); // 개발계 박준혁
				fileInfo.setBasicInfo("cab4676d20ff4da38bc65ce38e531c2b"); // 운영계 박준혁
				fileInfo.setFilename(other.getOtherName());
				fileInfo.setType(FileInfoType.OTHERS.getCode());
				String array[] = other.getOtherName().split("[.]");
				if (array[array.length - 1].length() < 10)
					fileInfo.setExtension(array[array.length - 1]);
				try {
					URL url = new URL(getUrl(other));
					Image image = ImageIO.read(url);
					fileInfo.setWidth(image.getWidth(null));
					fileInfo.setHeight(image.getHeight(null));
				} catch (Exception e) {
					// 이미지가아님
				}
				fileInfoRepo.save(fileInfo);
			}
		}
	}
	
}