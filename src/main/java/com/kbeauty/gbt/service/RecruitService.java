package com.kbeauty.gbt.service;

import com.kbeauty.gbt.dao.*;
import com.kbeauty.gbt.entity.domain.*;
import com.kbeauty.gbt.entity.enums.*;
import com.kbeauty.gbt.entity.view.*;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.MathUtil;
import com.kbeauty.gbt.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;

@Service
@Slf4j
public class RecruitService {

	public final static String NO_RECRUIT_IMG_PATH = "/images/no_user_img.png";
	public final static String NO_USER_IMG_PATH = "/images/no_user_img.png";


	@Autowired
	private RecruitRepo recruitRepo;

	@Autowired
	private ResourceRepo resourceRepo;

	@Autowired
	private RecruitMapper recruitMapper;

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
			}
			FileInfo fileInfo2 = fileInfoRepo.findByFilename(filename);
			if(fileInfo2 == null) {
				fileInfoRepo.save(fileInfo);
			}
		}else {
			
		}
		return fileInfo;
	}


	/**
	 * 목록 조회용
	 * 
	 * @param condition
	 * @return
	 */

	
	public List<RecruitView> getRecruitViewList(RecruitCondition condition) {
		List<RecruitView> list = new ArrayList<>();
		List<Recruit> recruitList = recruitMapper.getRecruitList(condition);

		Map<String, UserListView> userObjMap = getUserObjMap(recruitList);

		for (Recruit recruit : recruitList) {
			list.add(getRecruitViewByContent(recruit, userObjMap, condition.getSearchUserid()));
		}
		return list;
	}
	
	public List<RecruitView> getRecruitViewListNotAnother(RecruitCondition condition) {
		List<RecruitView> list = new ArrayList<>();
		List<Recruit> recruitList = recruitMapper.getRecruitList(condition);

		Map<String, UserListView> userObjMap = getUserObjMap(recruitList);

		for (Recruit recruit : recruitList) {
			list.add(getRecruitViewByContentNotAnother(recruit, userObjMap, condition.getSearchUserid()));
		}
		return list;
	}


	public int getRecruitListCnt(RecruitCondition condition) {

		return recruitMapper.getRecruitListCnt(condition);
	}


	public String getRecruitId() {
		return CommonUtil.getGuid();
	}

	public Recruit getRecruit(String recruitId) {
		Recruit recruit = recruitRepo.findByRecruitId(recruitId);
		return recruit;
	}

	public Resources getResource(String resourceId) {
		Resources resource = resourceRepo.findByResourceId(resourceId);
		return resource;
	}

	private void setRecruitUserInfo(Recruit recruit) {
		User user = userService.getUser(recruit.getUserId());
		if (user != null) {
			recruit.setUserName(user.getUserName());
			String userImgUrl = userService.getUrl(user);
			recruit.setUserImgUrl(userImgUrl);
		}
	}


//	TODO:공고 생성 란

	public RecruitView create(RecruitView view) {
		String dateStr = CommonUtil.getSysTime();
		Recruit recruit = view.getRecruit();
		String recruitId = recruit.getRecruitId();
		if (StringUtil.isEmpty(recruitId)) {
			recruitId = CommonUtil.getGuid();
			recruit.setRecruitId(recruitId);
		}

		String upperRecruitId = recruit.getUpperRecruitId();
//		upper = 대문자?
		Recruit upperRecruit = null;

		int upperDepth = 0;
		String upperPath = null;
		if (StringUtil.isEmpty(upperRecruitId)) {
			upperPath = "";
			upperDepth = -1;
		} else {
			upperRecruit = recruitRepo.findByRecruitId(upperRecruitId);
			upperDepth = upperRecruit.getDepth();
			upperPath = upperRecruit.getPath();
		}

		long seq = getRecruitSeq();
		String userId = recruit.getUserId();

		recruit.setSeq(seq);

		if (StringUtil.isEmpty(recruit.getRecruitType())) {
			if (upperRecruit != null) {
				recruit.setRecruitId(upperRecruit.getRecruitType());
			} else {
//				이벤트로 설정
				recruit.setRecruitType(RecruitType.EVENT.getCode());
			}
		}
//만약 활성화 상태면 활성화 get 코드
		if (StringUtil.isEmpty(recruit.getActive())) {
			recruit.setActive(RecruitActive.PASSIVE.getCode());
		}
//가입
		if (StringUtil.isEmpty(recruit.getStatus())) {
			recruit.setStatus(ContentStatus.REG.getCode());
		}

		recruit.setBasicInfo(dateStr, userId);

		int depth = upperDepth + 1;
		String path = StringUtil.getPath(seq, upperPath, depth);

		recruit.setPath(path);
		recruit.setDepth(depth);
		save(recruit);
//TODO : ??

		List<Resources> hashList = view.getHashList();
		if (hashList != null && !hashList.isEmpty()) {
			String hashId;
			for (Resources hash : hashList) {
				hashId = CommonUtil.getGuid();
				hash.setResourceId(hashId);
				hash.setContentId(recruitId);
				hash.setResourceType(ResourceType.HASHTAG.getCode());
				hash.setBasicInfo(dateStr, userId);
				save(hash);
			}
		}

		return view;
	}

	public long getRecruitSeq() {
		long seq = recruitMapper.getRecruitSeq();
		return seq;
	}

	
	public Recruit deleteRecruit(Recruit recruit, String userId) {
		String dateStr = CommonUtil.getSysTime();
		recruit = getRecruit(recruit.getRecruitId());
		recruit.setDelete();
		recruit.setBasicInfo(dateStr, userId);
		save(recruit);

//		List<Resources> resourceList = resourceRepo.findByContentId(content.getContentId());
//		if(resourceList.size()>0) {
//			for (Resources resources : resourceList) {
//				resources.setDelete();
//				save(resources);
//			}
//		}
		
		return recruit;
	}

	public Resources deleteResource(String resourceId, String userId) {
		String dateStr = CommonUtil.getSysTime();
		Resources resource = getResource(resourceId);
		resource.setDelete();
		resource.setBasicInfo(dateStr, userId);
		save(resource);
		return resource;
	}

	public Recruit saveRecruit(Recruit recruit, String userId) {
		String dateStr = CommonUtil.getSysTime();

		if (StringUtil.isEmpty(recruit.getRecruitId())) {
			recruit.setRecruitType(RecruitType.EVENT.getCode());
		}

		if (StringUtil.isEmpty(recruit.getActive())) {
			recruit.setActive(ContentActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(recruit.getStatus())) {
			recruit.setStatus(ContentStatus.REG.getCode());
		}

		recruit.setBasicInfo(dateStr, userId);
		save(recruit);

		return recruit;
	}

//TODO:수정 메소드
	
	public RecruitView saveRecruidView(RecruitView view, String loginId) {
		// Content
		String dateStr = CommonUtil.getSysTime();
		Recruit recruit = view.getRecruit();
		String recruitId = recruit.getRecruitId();

		if (StringUtil.isEmpty(recruit.getRecruitType())) {
			recruit.setRecruitType(RecruitType.EVENT.getCode());
		}

		if (StringUtil.isEmpty(recruit.getActive())) {
			recruit.setActive(RecruitActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(recruit.getStatus())) {
			recruit.setStatus(RecruitStatus.REG.getCode());
		}

		recruit.setBasicInfo(dateStr, loginId);
		save(recruit);

		return view;
	}

	/**
	 * 화면에서 조회할 때는 사용하는 함수
	 * 
	 * @param recruitId
	 * @param userId
	 * @return
	 */
	public RecruitView getRecruitView(String recruitId, String userId) {
		// TODO userId 를 사용해서 조회 가능한지 권한 체크해야 함. ==> 공통 사항.
		Recruit recruit = recruitRepo.findByRecruitId(recruitId);
		if (recruit == null) {
			return null;
		}

		String userIdx = recruit.getUserId();
		if (userId.equals(userIdx)) {
			// 무조건 조회가능
		} else {
			String viewType = recruit.getRecruitType();
			if (ContentViewType.ALL.getCode().equals(viewType)) {
				// 성공
			} else if (ContentViewType.ONLYME.getCode().equals(viewType)) {
				// 오류
				log.error("====== 권한 오류 (자신만 보기가능) =========");
			}  else if (ContentViewType.ADMIN.getCode().equals(viewType)) {
				// userId로 유저 권한 체크
				boolean isAdmin = userService.isAdmin(userId);
				if (!isAdmin) {
					log.error("====== 권한 오류 (Admin권한 필요) =========");
				}
			}
		}

		RecruitView view = getRecruitViewByContent(recruit, userId);

		return view;
	}

	/**
	 * 내부에서만 사용하는 함수 ==> 화면에서 조회할 때는 위에 함수를 사용해야 함.
	 * 
	 * @param recruitId
	 * @return
	 */
	public RecruitView getRecruitView(String recruitId) {
		Recruit recruit = recruitRepo.findByRecruitId(recruitId);
		if (recruit == null) {
			return null;
		}

		RecruitView view = getRecruitViewByContent(recruit, null);
		return view;
	}

	public RecruitView getRecruitViewAllResource(String recruitId) {
		Recruit recruit = recruitRepo.findByRecruitId(recruitId);
		if (recruit == null) {
			return null;
		}

		RecruitView view = getRecruitViewByContent(recruit, null, null, true);
		return view;
	}

	private Map<String, UserListView> getUserObjMap(List<Recruit> recruitList) {
		List<String> userids = new ArrayList<String>();
		for (Recruit recruit : recruitList) {
			userids.add(recruit.getUserId());
		}

		Map<String, UserListView> userObjMap = userService.getUserObjMap(userids);

		return userObjMap;
	}

	private RecruitView getRecruitViewByContent(Recruit recruit, String searchUserid) {
		return getRecruitViewByContent(recruit, null, searchUserid);
	}

	private RecruitView getRecruitViewByContent(Recruit recruit, Map<String, UserListView> userObjMap,
			String searchUserid) {
		return getRecruitViewByContent(recruit, userObjMap, searchUserid, false);
	}

	private RecruitView getRecruitViewByContent(Recruit recruit, Map<String, UserListView> userObjMap,
			String searchUserid, boolean flag ) {

		RecruitView view = new RecruitView();
		String recruitId = recruit.getRecruitId();
		if (userObjMap == null) {
			User user = userService.getUser(recruit.getUserId());
			if (user != null) {
				recruit.setUserName(user.getUserName());
				String userImgUrl = userService.getUrl(user);
				recruit.setUserImgUrl(userImgUrl);

			}
		} else {
			UserListView userListView = userObjMap.get(recruit.getUserId());
			if (userListView == null) {
				recruit.setUserName("미등록자");
				recruit.setUserImgUrl(NO_USER_IMG_PATH);
			} else {
				recruit.setUserName(userListView.getUserName());
				recruit.setUserImgUrl(userListView.getImgUrl());
			}
		}

		if (recruit.isMainImg()) {
			recruit.setMainImage(getUrl(recruit));
		} else {
			recruit.setMainImage(NO_RECRUIT_IMG_PATH);
		}

		recruit.setEnumName();

		List<Resources> resourceList = new ArrayList<>();


		ContentCondition condition = new ContentCondition();

		view.setRecruit(recruit);
		view.setResourceList(resourceList);


		return view;
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
					Recruit re = recruitRepo.findByRecruitId(inputResource.getUrl());
					if(re==null) {
						long searchContentSeq = Long.parseLong(inputResource.getUrl());
						re = recruitRepo.findById(searchContentSeq).get();
						oldResource.setResourceName("" + searchContentSeq);
					}else {
						oldResource.setResourceName("" + re.getSeq());
					}
					try {
						RecruitView view = getRecruitViewByContent(re, null);
						re = view.getRecruit();
						oldResource.setResourceTitle(re.getTitle());
						oldResource.setResourceContent(re.getContent());
						if(re.getMainImage()!=null && !"".equals(re.getMainImage())) {
							oldResource.setDir(re.getMainImage());
						}
						oldResource.setUrl(re.getRecruitId());
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
			
		}

		resource.setUrl(url); // 저장용

		return resource;
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

	public Resources createFile(MultipartFile file, String userId, String recruitId, String title, String recruit,
			String url) throws IOException {
		Resources resource = getResources(file, userId, recruitId, title, recruit, url);

		return createFile(file, userId, resource);
	}

	public Resources createFile(MultipartFile file, String userId, Resources resource) throws IOException {

		if (resource.isStorage()) {
			uploadSerivce.saveUserImg(file, resource.getFilename());
		}

//파일을 먼저 저장한 이후에 테이블에 저장한다. 		
		String dateStr = CommonUtil.getSysTime();
		resource.setBasicInfo(dateStr, userId);
		save(resource);

		return resource;
	}

	/**
	 * 처리 전용 (조회에 사용하면 안됨)
	 * 
	 * @param
	 * @return
	 */
	public void save(Recruit recruit) {
		recruitRepo.save(recruit);
	}

	public void save(Resources resource) {
		resourceRepo.save(resource);
	}


	private String getUrl(Resources resource) {
		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(resource.getDir()).append("/").append(resource.getFilename());
		return sb.toString();

	}

	public String getUrl(Recruit recruit) {
		if (StringUtil.isEmpty(recruit.getMainImage())) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(recruit.getMainImage());
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
		}

		resource.setResourceId(resourceId);
		resource.setResourceName(uploadFileName);
		resource.setDir(dir);
		resource.setFilename(fileName);

		uploadSerivce.saveUserImg(file, fileName);
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
	
	private RecruitView getRecruitViewByContentNotAnother(Recruit recruit, Map<String, UserListView> userObjMap,
			String searchUserid ) {  // 관리자페이지 컨텐츠리스트 띄우는데 다른거 가져오는것 때매 너무 오래걸려서 하나빼놨음

		RecruitView view = new RecruitView();
		String recruitId = recruit.getRecruitId();
		if (userObjMap == null) {
			User user = userService.getUser(recruit.getUserId());
			if (user != null) {
				recruit.setUserName(user.getUserName());
				String userImgUrl = userService.getUrl(user);
				recruit.setUserImgUrl(userImgUrl);

			}
		} else {
			UserListView userListView = userObjMap.get(recruit.getUserId());
			if (userListView == null) {
				recruit.setUserName("미등록자");
				recruit.setUserImgUrl(NO_USER_IMG_PATH);
			} else {
				recruit.setUserName(userListView.getUserName());
				recruit.setUserImgUrl(userListView.getImgUrl());
			}
		}

		if (recruit.isMainImg()) {
			recruit.setMainImage(getUrl(recruit));
		} else {
			recruit.setMainImage(NO_RECRUIT_IMG_PATH);
		}

		recruit.setEnumName();

		view.setRecruit(recruit);


		return view;
	}


	
}