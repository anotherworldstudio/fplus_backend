package com.kbeauty.gbt.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.ai.GoogleFaceBuilder;
import com.kbeauty.gbt.ai.ImgCrop;
import com.kbeauty.gbt.ai.VisionFace;
import com.kbeauty.gbt.dao.AppleLoginRepo;
import com.kbeauty.gbt.dao.FollowRepo;
import com.kbeauty.gbt.dao.LessonMapper;
import com.kbeauty.gbt.dao.PointMapper;
import com.kbeauty.gbt.dao.UserFaceRepo;
import com.kbeauty.gbt.dao.UserMapper;
import com.kbeauty.gbt.dao.UserRepo;
import com.kbeauty.gbt.dao.UserSkinRepo;
import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.AppleLogin;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.domain.UserSkin;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FollowActionType;
import com.kbeauty.gbt.entity.enums.FollowSearchType;
import com.kbeauty.gbt.entity.enums.FollowStatus;
import com.kbeauty.gbt.entity.enums.FollowType;
import com.kbeauty.gbt.entity.enums.Orientation;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.entity.enums.UserAccountType;
import com.kbeauty.gbt.entity.enums.UserFaceStatus;
import com.kbeauty.gbt.entity.enums.UserFaceWho;
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.enums.UserStatus;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.FollowCondition;
import com.kbeauty.gbt.entity.view.FollowListView;
import com.kbeauty.gbt.entity.view.ImageData;
import com.kbeauty.gbt.entity.view.ModelCondition;
import com.kbeauty.gbt.entity.view.ModelUserList;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.UserCondition;
import com.kbeauty.gbt.entity.view.UserFaceView;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.entity.view.UserView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.ImageUtil;
import com.kbeauty.gbt.util.StringUtil;

@Service
public class UserService extends CommonService {

	public final static String USER_LIST_KEY = "userList";
	public final static String USER_ID_KEY = "userId";

	@Autowired
	private UserRepo repo;

	@Autowired
	private FollowRepo followRepo;

	@Autowired
	private UserSkinRepo userSkinRepo;

	@Autowired
	private StorageService storageSerivce;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private LessonMapper lessonMapper;

	@Autowired
	private UserFaceRepo userFaceRepo;

	@Autowired
	private PointMapper pointMapper;
	
	@Autowired
	private AppleLoginRepo appleLoginRepo;

	@Autowired
	private StorageService uploadSerivce;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucket;

	@Value("${spring.cloud.gcp.storage.bucket.user}")
	private String userFolder;

	@Value("${spring.cloud.gcp.storage.url}")
	private String storageUrl;

	@Value("${spring.cloud.gcp.storage.bucket.training}")
	private String trainingFolder;

	@Value("${beautage.ai.server.url}")
	private String aiServerUrl;

	@Value("${spring.cloud.gcp.storage.bucket.ai}")
	private String aiFolder;

	public User save(User user) {
		boolean isNew = false;

		String userId = user.getUserId();
		if (StringUtil.isEmpty(userId)) {
			isNew = true;
			userId = CommonUtil.getGuid();
			user.setUserId(userId);
		}

		// userName, email 중복 체크
		String userName = user.getUserName();

		if (user.getUserName() != null) {
			List<User> useList = repo.findByUserName(userName);
			if (!StringUtil.isEmpty(useList)) {
				for (User user2 : useList) {
					if (user2.isSame(user)) {
						continue;
					} else {
						throw new MessageException(ErrMsg.DUPLICATE);
					}
				}
			}
		}

		String email = user.getEmail();
		String oauthType = user.getOauthType();
		if (email != null) {
			try {
				User findByEmail = repo.findByEmailAndOauthType(email, oauthType);
				if (findByEmail != null) {
					if (!findByEmail.isSame(user)) {
						throw new MessageException(ErrMsg.DUPLICATE_EMAIL);
					}
				}
			} catch (Exception e) {
				throw new MessageException(ErrMsg.DUPLICATE_EMAIL);
			}
		}

		String dateStr = CommonUtil.getSysTime();
		user.setBasicInfo(dateStr, userId);

//		// 
//		if( ! isNew) {
//			// 기존 사용자 조회 이후에 해당 항목만 수정함
//			// 이멜/사용자명/생년월일/성별/휴대폰/국적//사용자구분//로그인구분/마켓팅/상태/비고
//			userId = user.getUserId();
//			User oldUser = getUser(userId);
//			oldUser.setEmail(user.getEmail());
//			oldUser.setUserName(user.getUserName());
//			oldUser.setBirthDay(user.getBirthDay());
//			oldUser.setSex(user.getSex());
//			oldUser.setCellphone(user.getCellphone());
//			oldUser.setCountry(user.getCountry());
//			oldUser.setUserRole(user.getUserRole());
//			oldUser.setOauthType(user.getOauthType());
//			oldUser.setMarketingYn(user.getMarketingYn());
//			oldUser.setStatus(user.getStatus());			
//			oldUser.setComment(user.getComment());
//			
//			repo.save(oldUser); 
//			return oldUser;
//		}else {
//		}
		repo.save(user);
		return user;
	}

	public User saveImg(MultipartFile faceImg, User user, String uploadFileName) throws IOException {

		String orgFileName = faceImg.getOriginalFilename();
		String imgId = CommonUtil.getGuid();
		String neoFileName = FileUtil.getNeoFileName(imgId, orgFileName); // 확장자는 여기서 붙
		String fileName = FileUtil.getStoragePath(userFolder, user.getUserId(), neoFileName, StoragePath.CONTENT_USER);

		String dir = bucket;
		user.setImageDir(dir);
		user.setImageName(fileName);

		uploadSerivce.saveUserImg(faceImg, fileName);

		user = save(user);
		setImgUrl(user);

		return user;
	}

	public User deleteImg(User user) {
		user.setImageDir(null);
		user.setImageName(null);

		user = save(user);
		setImgUrl(user);

		return user;
	}

	public User deleteUser(User user) {

		String dateStr = CommonUtil.getSysTime();
		user.setBasicInfo(dateStr, user.getUserId());
		user.setDelete();

		repo.save(user);
		return user;
	}

	public UserSkin saveUserSkin(UserSkin userSkin) {
		String userId = userSkin.getUserId();

		UserSkin oldUserSkin = userSkinRepo.findByUserId(userId);
		if (oldUserSkin == null) { // 신규건
			// 기존 입력으로 신규 처리한다.
		} else { // 변경건
			// seq를 추가한다.
			userSkin.setSeq(oldUserSkin.getSeq());
		}

		setBasicInfo(userSkin, userSkin.getUserId());

		userSkinRepo.save(userSkin);
		return userSkin;
	}

	public User getUserByEmail(String email) {
		return repo.findByEmail(email);
	}

	public User getUserByEmailAndOauthType(String email, String oauthType) {
		return repo.findByEmailAndOauthType(email, oauthType);
	}

	public User findByUserAppleKey(User loginUser) {
//		return repo.findByUserAppleKeyAndOauthType(loginUser.getUserAppleKey(),loginUser.getOauthType());
		return userMapper.findByUserAppleKey(loginUser);
	}

	public Map<String, String> getUserMap(List<String> userids) {
		Map<String, String> userMap = new HashMap<>();

		Map<String, List<String>> condition = new HashMap<String, List<String>>();
		condition.put(USER_LIST_KEY, userids);

		List<Map<String, String>> userMapList = userMapper.getUserMap(condition);
		for (Map<String, String> map : userMapList) {
			userMap.put(map.get("userid"), map.get("username"));
		}

		return userMap;
	}

	public User getUser(String userid) {
		return repo.findByUserId(userid);
	}

	public List<User> getUserByUserName(String userName) {
		return repo.findByUserName(userName);
	}

	public UserView getUserView(String userId, String loginUserid) {
		UserView view = new UserView();
		User user = repo.findByUserId(userId);
		if (user.isDelete()) {
			user = null;
		}

		if (user == null)
			return null;
		user.setMarketingYn(YesNo.setNo(user.getMarketingYn()));

		String userRoleName = CommonUtil.getValue(UserRole.values(), user.getUserRole());
		user.setUserRoleName(userRoleName);

		setImgUrl(user);

		UserSkin userSkin = userSkinRepo.findByUserId(userId);
		view.setUser(user);
		view.setUserSkin(userSkin);

		if (!loginUserid.equals(userId)) {
			Follow follow = getFollowByUserId(loginUserid, userId);
			if (follow != null) {
				follow.removeResInfo(); // 상대방 블락 처리에 대해서 정보를 제거한다.
				view.setFollow(follow);
			}
		}

		UserCondition condition = new UserCondition();
		condition.setUserId(userId);

//		int followCnt = userMapper.getFollowCnt(condition);
//		int followingCnt = userMapper.getFollowingCnt(condition);
//		int likeCnt = userMapper.getLikeCnt(condition);
//		int totalMileage = getHaveMileage(userId);
//		int totalExp = getHaveExp(userId);
		UserFace myFace = userFaceRepo.findByUserIdAndWho(userId, UserFaceWho.MY.getCode());
		if (myFace != null) {
			view.setUserFace(myFace);
		}
//		view.setFollowCnt(followCnt);
//		view.setFollowingCnt(followingCnt);
//		view.setLikeCnt(likeCnt);
//		view.setTotalMileage(totalMileage);
//		view.setTotalExp(totalExp);

		return view;
	}

	private void setImgUrl(User user) {
		user.setImgUrl(getUrl(user)); // 사용자 이미지 위치 설정
	}

	public List<UserListView> getUserList(UserCondition condition) {
		List<UserListView> list = userMapper.getUserList(condition);
		setUserData(list);
		return list;
	}

	public Map<String, UserListView> getUserObjMap(List<String> userids) {

		Map<String, List<String>> condition = new HashMap<String, List<String>>();
		condition.put(USER_LIST_KEY, userids);

		List<UserListView> list = userMapper.getUserObjMap(condition);
		setUserData(list);

		Map<String, UserListView> userObjMap = new HashMap<>();
		for (UserListView userListView : list) {
			userObjMap.put(userListView.getUserId(), userListView);
		}

		return userObjMap;
	}

	private void setUserData(List<UserListView> list) {
		for (UserListView userListView : list) {
			if (StringUtil.isEmpty(userListView.getCellphone())) {
				userListView.setCellphone("미입력");
			}

			if (YesNo.isYes(userListView.getMarketingYn())) {
				userListView.setMarketingYn("동의");
			} else {
				userListView.setMarketingYn("미동의");
			}

			userListView.setStatusName(CommonUtil.getValue(UserStatus.values(), userListView.getStatus()));

			userListView.setUserRoleName(CommonUtil.getValue(UserRole.values(), userListView.getUserRole()));

			userListView.setImgUrl(getUrl(userListView));

			userListView.setTotalMileage(getHaveMileage(userListView.getUserId()));
			userListView.setTotalExp(getHaveExp(userListView.getUserId()));

		}
	}

	public int getUserListCnt(UserCondition condition) {
		int result = userMapper.getUserListCnt(condition);
		return result;
	}

	public List<ModelUserList> getModelEventList(ModelCondition condition) {
		List<ModelUserList> list = userMapper.getModelEventList(condition);

		List<String> userids = new ArrayList<String>();
		for (ModelUserList modelUser : list) {
			userids.add(modelUser.getUserId());
		}

		Map<String, UserListView> userObjMap = getUserObjMap(userids);
		UserListView userListView = null;
		for (ModelUserList modelUser : list) {
			userListView = userObjMap.get(modelUser.getUserId());
			modelUser.setSeq(userListView.getSeq());
			modelUser.setUserName(userListView.getUserName());
			modelUser.setEmail(userListView.getEmail());
			modelUser.setImgUrl(getUrl(userListView));
			modelUser.setStatus(userListView.getStatus());
			modelUser.setBirthDay(userListView.getBirthDay());
			modelUser.setSex(userListView.getSex());
			modelUser.setCellphone(userListView.getCellphone());
			modelUser.setStatusName(CommonUtil.getValue(UserStatus.values(), userListView.getStatus()));
		}

		return list;
	}

	public int getModelEventListCnt(ModelCondition condition) {
		int result = userMapper.getModelEventListCnt(condition);
		return result;
	};

	/**
	 * 내가 상대방을 block 처리했는지?
	 * 
	 * @return
	 */
	private boolean isBlocking(Follow follow, FollowActionType actionType) {
		String userId = null;
		String followId = null;
		boolean isBlock = false;
		Follow block = null;
		switch (actionType) {
		case REQUEST:
		case UNFOLLOWING:
		case CANCEL_UNFOLLOWING:
			userId = follow.getUserId();
			followId = follow.getFollowerId();
			break;

		case CONFIRM:
		case DELETE:
		case CANCEL_DELETE:
			userId = follow.getFollowerId();
			followId = follow.getUserId();
			break;

		default:
			break;
		}

		block = userMapper.getBlockByUserId(userId, followId);
		isBlock = block != null;
		return isBlock;
	}

	/**
	 * 상대방이 나를 block 처리했는지?
	 * 
	 * @return
	 */
	private boolean isBlocked(Follow follow, FollowActionType actionType) {
		String userId = null;
		String followId = null;
		boolean isBlocked = false;
		Follow block = null;
		switch (actionType) {
		case REQUEST:
		case UNFOLLOWING:
		case CANCEL_UNFOLLOWING:
			userId = follow.getFollowerId();
			followId = follow.getUserId();
			break;

		case CONFIRM:
		case DELETE:
		case CANCEL_DELETE:
			userId = follow.getUserId();
			followId = follow.getFollowerId();
			break;

		default:
			break;
		}

		block = userMapper.getBlockByUserId(userId, followId);
		isBlocked = block != null;
		return isBlocked;
	}

	public boolean isAdmin(String loginUser) {
		User user = getUser(loginUser);
		return UserRole.ADMIN.getCode().equals(user.getUserRole());
	}

	public boolean isRealFollow(String loginUser, String followerId) {
		Follow oldFollow = getFollowByUserId(loginUser, followerId);
		return YesNo.isYes(oldFollow.getFollowYn());
	}

	public Follow getFollowByUserId(String loginUser, String followerId) {
		Follow follow = new Follow();
		follow.setUserId(loginUser);
		follow.setFollowerId(followerId);
		Follow oldFollow = userMapper.getOldFollowByUserId(follow);
		return oldFollow;
	}

	public Follow saveFollow(Follow follow, String userId, FollowActionType actionType) {
		// followId가 있으면 이전꺼 조회
		Follow oldFollow = null;
		if (follow.getFollowId() != null) {
			oldFollow = followRepo.findByFollowId(follow.getFollowId());
		}

		if (StringUtil.isEmpty(follow.getFollowId())) {
			String followId = CommonUtil.getGuid();
			follow.setFollowId(followId);
		}

		boolean blocking = isBlocking(follow, actionType);
		if (blocking) {
			throw new MessageException(ErrMsg.ALREADY_BLOCK);
		}

		boolean blocked = isBlocked(follow, actionType);
		follow.setFollowType(FollowType.FOLLOW.getCode());

		switch (actionType) {
		case REQUEST:
			oldFollow = userMapper.getOldFollowByUserId(follow);
			if (oldFollow != null) {
				throw new MessageException(ErrMsg.ALREADY_FOLLOW_REQ);
			}

			String followerId = follow.getFollowerId();
			User user = getUser(followerId);
			if (UserAccountType.CLOSE.getCode().equals(user.getAccountType())) { // 비공개 계정인 경우
				follow.setReqType(FollowStatus.REQ.getCode());
				follow.setResType(FollowStatus.REQ.getCode());
				follow.setReqBlock(YesNo.NO.getVal());
				follow.setResBlock(YesNo.getYN(blocked));

				follow.setFollowYn(YesNo.NO.getVal());
			} else {
				follow.setReqType(FollowStatus.CON.getCode());
				follow.setResType(FollowStatus.CON.getCode());
				follow.setReqBlock(YesNo.NO.getVal());
				follow.setResBlock(YesNo.getYN(blocked));

				if (blocked) {
					follow.setFollowYn(YesNo.NO.getVal());
				} else {
					follow.setFollowYn(YesNo.YES.getVal());
				}
			}

			setBasicInfo(follow, userId);
			followRepo.save(follow);

			return follow;

		case WITHDRAW:
			if (!FollowStatus.REQ.getCode().equals(oldFollow.getReqType())) {
				throw new MessageException(ErrMsg.NO_FOLLOW);
			}
			oldFollow.setReqType(FollowStatus.WDR.getCode());
			break;
		case CANCEL_WITHDRAW:
			if (!FollowStatus.WDR.getCode().equals(oldFollow.getReqType())) {
				throw new MessageException(ErrMsg.NO_FOLLOW);
			}
			oldFollow.setReqType(FollowStatus.REQ.getCode());
			break;
		case UNFOLLOWING:
			if (!FollowStatus.CON.getCode().equals(oldFollow.getReqType())) {
				throw new MessageException(ErrMsg.NO_FOLLOW);
			}
			oldFollow.setReqType(FollowStatus.UNF.getCode());
			oldFollow.setResType(FollowStatus.UNF.getCode());
			break;
		case CANCEL_UNFOLLOWING:
			if (!FollowStatus.UNF.getCode().equals(oldFollow.getReqType())) {
				throw new MessageException(ErrMsg.NO_FOLLOW);
			}
			oldFollow.setReqType(FollowStatus.CON.getCode());
			oldFollow.setResType(FollowStatus.CON.getCode());
			break;
		case CONFIRM:
			if (!FollowStatus.REQ.getCode().equals(oldFollow.getResType())) {
				throw new MessageException(ErrMsg.NO_FOLLOW);
			}

			if (FollowStatus.REQ.getCode().equals(oldFollow.getReqType())) {
				oldFollow.setReqType(FollowStatus.CON.getCode());
			}

			oldFollow.setResType(FollowStatus.CON.getCode());

			break;
		case DELETE:
			if (FollowStatus.DEL.getCode().equals(oldFollow.getResType())) { // DEL 인 경우만 오류
				throw new MessageException(ErrMsg.ALREADY_DELETE);
			}
			oldFollow.setResType(FollowStatus.DEL.getCode());
			break;
		case CANCEL_DELETE:
			if (!FollowStatus.DEL.getCode().equals(oldFollow.getResType())) {
				throw new MessageException(ErrMsg.NO_FOLLOW);
			}

			if (FollowStatus.REQ.getCode().equals(oldFollow.getReqType())
					|| FollowStatus.WDR.getCode().equals(oldFollow.getReqType())) {
				oldFollow.setResType(FollowStatus.REQ.getCode());
			} else {
				oldFollow.setResType(FollowStatus.CON.getCode());
			}

			break;
		default:
			break;
		}

		String followYn = YesNo.NO.getVal();
		// block : 둘다 N / type이 둘다 CON 일 경우, Follow 데이터를 볼 수 있다.
		if (FollowStatus.CON.getCode().equals(oldFollow.getReqType())
				&& FollowStatus.CON.getCode().equals(oldFollow.getResType())
				&& YesNo.NO.getVal().equals(oldFollow.getReqBlock())
				&& YesNo.NO.getVal().equals(oldFollow.getResBlock())) {

			followYn = YesNo.YES.getVal();
		}

		oldFollow.setFollowYn(followYn);
		followRepo.save(oldFollow);

		return oldFollow;
	}

	/**
	 * getFollowList 에서 마팔 조회용
	 * 
	 * @param userId
	 * @param userids
	 * @return
	 */
	private Set<String> getFollowingSet(String userId, List<String> userids) {

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(USER_LIST_KEY, userids);
		condition.put(USER_ID_KEY, userId);

		Set<String> followSet = userMapper.getFollowingSet(condition);

		return followSet;
	}

	/**
	 * getFollowingList 에서 사용하는 마팔 조회용
	 * 
	 * @param userId
	 * @param userids
	 * @return
	 */
	private Set<String> getFollowSet(String userId, List<String> userids) {

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put(USER_LIST_KEY, userids);
		condition.put(USER_ID_KEY, userId);

		Set<String> followSet = userMapper.getFollowSet(condition);

		return followSet;
	}

	public List<FollowListView> getFollowList(FollowCondition condition) {

		condition.setSearchType(FollowSearchType.FOLLOW.getCode());

		// 1. 유저 아이디만 입력
		String userId = condition.getUserId();
		condition.setUserId(null);
		condition.setFollowerId(userId);
		// 4. userName은 화면에서 입력받음
		// 5. 나머지 값들은 현재 미정
		List<FollowListView> followList = userMapper.getFollowList(condition);
		List<String> userids = new ArrayList<String>();
		for (FollowListView followListView : followList) {
			userids.add(followListView.getUserId());
		}

		Set<String> followingSet = getFollowingSet(userId, userids);

		for (FollowListView followListView : followList) {
			followListView.setImgUrl(getUrl(followListView));
			// 마팔 확인로직 여기에 추가
			if (followingSet.contains(followListView.getUserId())) {
				followListView.setTogether(1); // 마팔
			}
		}

		return followList;
	}

	public int getFollowListCnt(FollowCondition condition) {
		return userMapper.getFollowListCnt(condition);
	}

	public List<FollowListView> getFollowingList(FollowCondition condition) {

		condition.setSearchType(FollowSearchType.FOLLOWING.getCode());

		// 1. 유저 아이디만 입력
		String userId = condition.getUserId();
//		condition.setUserId(null);
//		condition.setUserId(userId);

		// 4. userName은 화면에서 입력받음
		// 5. 나머지 값들은 현재 미정

		List<FollowListView> followList = userMapper.getFollowingList(condition);

		List<String> userids = new ArrayList<String>();
		for (FollowListView followListView : followList) {
			userids.add(followListView.getUserId());
		}

		Set<String> followSet = getFollowSet(userId, userids);

		for (FollowListView followListView : followList) {
			followListView.setImgUrl(getUrl(followListView));
			// 마팔 확인로직 여기에 추가
			if (followSet.contains(followListView.getFollowerId())) {
				followListView.setTogether(1); // 마팔
			}
		}

		return followList;
	}

	public int getFollowingListCnt(FollowCondition condition) {
		return userMapper.getFollowingListCnt(condition);
	}

	private String getUrl(UserListView userListView) {
		String imageDir = userListView.getImageDir();
		String imageName = userListView.getImageName();

		return getUrl(imageDir, imageName);
	}

	private String getUrl(FollowListView userListView) {
		String imageDir = userListView.getImageDir();
		String imageName = userListView.getImageName();

		return getUrl(imageDir, imageName);
	}

	private String getUrl(String imageDir, String imageName) {
		if (!StringUtil.isEmpty(imageDir) && imageDir.startsWith("http")) {
			return imageDir;
		}

		if (StringUtil.isEmpty(imageName)) {
			return "/images/no_user_img.png";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(imageDir).append("/").append(imageName);
		return sb.toString();
	}

	public String getUrl(User user) {
		String imageDir = user.getImageDir();

		if (!StringUtil.isEmpty(imageDir) && imageDir.startsWith("http")) {
			return imageDir;
		}

		if (StringUtil.isEmpty(user.getImageName())) {
			return "/images/no_user_img.png";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(imageDir).append("/").append(user.getImageName());

		return sb.toString();
	}

	public List<UserListView> searchUserName(UserCondition condition) {
		List<UserListView> userListViewList = userMapper.searchUserName(condition);
		for (UserListView userListView : userListViewList) {
			userListView.setImgUrl(getUrl(userListView));
		}

		return userListViewList;
	}

	private ImageData getImageData(String imgUrl) {
		ImageData img = new ImageData();
		img.setImageUrl(imgUrl);
		if ("".equals(imgUrl)) {
			img.setWidthHeight();
		}
		return img;
	}

	public int getHaveMileage(String userId) {
		int getTotal, useTotal;
		getTotal = pointMapper.getTotalGetMileage(userId);
		if (getTotal == 0)
			return getTotal;
		useTotal = pointMapper.getTotalUseMileage(userId);

		return getTotal - useTotal;
	}

	public int getHaveExp(String userId) {
		int getTotal, useTotal;
		getTotal = pointMapper.getTotalGetExp(userId);
		if (getTotal == 0)
			return getTotal;
		useTotal = pointMapper.getTotalUseExp(userId);

		return getTotal - useTotal;
	}

	public List<UserFace> getUserFaceList(UserCondition condition) {
		List<UserFace> list = userMapper.getUserFaceList(condition);
		return list;
	}

	public UserFace saveMyUserFace(MultipartFile orgFaceImg, String fileName, UserFace userFace, String userId)
			throws Exception {
		String imgId = CommonUtil.getGuid(); // skin id // 랜덤 GUID 생성
		String extention = FileUtil.getExtension(fileName); // 파일 확장자명
		String neoFileName = FileUtil.getNeoFileName(imgId, fileName); // guid.확장자로 파일명 재설정 ex)G837V1227XV92BG72E1.jpg
		String aiFileName = "T_" + neoFileName;
		String storagePath = FileUtil.getStoragePath(trainingFolder, userId, neoFileName, StoragePath.TRAINING);

		// 1. resize img
		BufferedImage bFaceImg = ImageIO.read(orgFaceImg.getInputStream()); // 사진 가져오기
		Orientation orient = ImageUtil.setOrient(orgFaceImg.getInputStream()); // 기울기 체크
		BufferedImage resizeFaceImg = ImageUtil.rotate(bFaceImg, orient, true); // 조정
		String url = storageSerivce.saveStorage(resizeFaceImg, storagePath, orgFaceImg.getContentType());

		// 2. img 구글 전송
		String gsUrl = FileUtil.getGsUrl(bucket, storagePath);
		GoogleFaceBuilder builder = new GoogleFaceBuilder();

		// 3. 얼굴 정보리턴한다.
		VisionFace visionFace = builder.getFace(gsUrl);
		if (visionFace == null) {
			throw new MessageException(ErrMsg.AI_FACE_ERR);
		}

		// 기울기 조정 부분
		visionFace.setWidth(resizeFaceImg.getWidth());
		visionFace.setHeight(resizeFaceImg.getHeight());
		Orientation orientation = visionFace.genOrientation();
		resizeFaceImg = ImageUtil.rotate(resizeFaceImg, orientation, false);

		BufferedImage treatyImg = CroppingUtil.cropNResize(resizeFaceImg, visionFace, CommonConstants.TREATY_SIZE);
		visionFace.adjustDemension(treatyImg.getWidth(), treatyImg.getHeight()); // 진단 이미지에 맞춰서 landmark 조정
		int width = treatyImg.getWidth();
		int height = treatyImg.getHeight();
		ImgCrop imgCrop = new ImgCrop(width, height);
		imgCrop.setChild();

		BufferedImage aiImg = ImageUtil.resize(treatyImg, CommonConstants.AI_SIZE);
		// aiImg 분석용 이미지를 storage에 저장한다.
		storagePath = FileUtil.getStoragePath(aiFolder, userId, aiFileName, StoragePath.AI_USER_DAILY);
		String aiUrl = storageSerivce.saveStorage(aiImg, storagePath, orgFaceImg.getContentType());

		User user = repo.findByUserId(userId);
		UserFace newUserFace = userFaceRepo.findByUserIdAndWho(userId, UserFaceWho.MY.getCode());

		if (newUserFace == null) {
			newUserFace = new UserFace();
			String userFaceId = CommonUtil.getGuid(); // skin id // 랜덤 GUID 생성
			newUserFace.setUserFaceId(userFaceId);
		}
		newUserFace.setUserId(userId);
		newUserFace.setThumbnail(aiUrl);
		newUserFace.setSkinTone(userFace.getSkinTone());
		newUserFace.setAge(userFace.getAge());
		newUserFace.setFacialContour(userFace.getFacialContour());
		newUserFace.setGender(userFace.getGender());
		newUserFace.setSeasonColor(userFace.getSeasonColor());
		newUserFace.setFaceName(user.getUserName());
		newUserFace.setStatus(UserFaceStatus.BASIC.getCode());
		newUserFace.setWho(UserFaceWho.MY.getCode());
		newUserFace.setBasicInfo(userId);
		userFaceRepo.save(newUserFace);
		return userFace;
	}

	public User setUserRole(User user) {
		User oldUser = repo.findByUserId(user.getUserId());
		oldUser.setUserRole(user.getUserRole());
		oldUser.setBasicInfo(user.getUserId());
		repo.save(oldUser);
		return oldUser;
	}
	public AppleLogin save(AppleLogin appleLogin) {
		appleLoginRepo.save(appleLogin);
		return appleLogin;
	}
	public AppleLogin findAppleLogin(AppleLogin appleLogin) {
		AppleLogin returnAppleLogin = appleLoginRepo.findByUserIdentifier(appleLogin.getUserIdentifier());
		return returnAppleLogin;
	}
	
}
