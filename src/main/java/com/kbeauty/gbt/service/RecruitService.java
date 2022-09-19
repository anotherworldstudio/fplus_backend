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



	/**
	 * 목록 조회용
	 * 
	 * @param condition
	 * @return
	 */


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

//	TODO: 새로 조회
	public int getPremiumByListCnt(RecruitByCondition condition) {
		return recruitMapper.getPremiumByListCnt(condition);
	}

	public List<RecruitByView> getPremiumList(RecruitByCondition condition) {
		List<RecruitByView> list = new ArrayList<>();
//		RecruitByView view = new RecruitByView();
		List<Premium> premiumList = recruitMapper.getPremiumByList(condition);
		for (Premium pm: premiumList ) {
			RecruitByView view = new RecruitByView();
			view.setPremium(pm);
			list.add(view);
		}

		return list;
	}


	public String getRecruitId() {
		return CommonUtil.getGuid();
	}

	public Recruit getRecruit(String recruitId) {
		Recruit recruit = recruitRepo.findByRecruitId(recruitId);
		return recruit;
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

		return recruit;
	}


	public Recruit saveRecruit(Recruit recruit, String userId) {
		String dateStr = CommonUtil.getSysTime();
//		TODO:
		if(StringUtil.isEmpty(recruit.getRecruitType())) {
			recruit.setRecruitType(RecruitType.EVENT.getCode());
		}

		if (StringUtil.isEmpty(recruit.getActive())) {
			recruit.setActive(RecruitActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(recruit.getStatus())) {
			recruit.setStatus(RecruitStatus.REG.getCode());
		}

		recruit.setBasicInfo(dateStr, userId);
		save(recruit);

		return recruit;
	}

//TODO:수정 메소드


	/**
	 * 화면에서 조회할 때 사용하는 함수
	 *
	 * @param
	 * @param
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