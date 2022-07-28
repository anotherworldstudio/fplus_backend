package com.kbeauty.gbt.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import javax.persistence.Column;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbeauty.gbt.ai.Dimension;
import com.kbeauty.gbt.ai.NeoLandmark;
import com.kbeauty.gbt.ai.VisionFace;
import com.kbeauty.gbt.dao.SkinDoctorRepo;
import com.kbeauty.gbt.dao.SkinGroupRepo;
import com.kbeauty.gbt.dao.SkinInfoRepo;
import com.kbeauty.gbt.dao.SkinItemRepo;
import com.kbeauty.gbt.dao.SkinLevelRepo;
import com.kbeauty.gbt.dao.SkinMapper;
import com.kbeauty.gbt.dao.SkinRankingRepo;
import com.kbeauty.gbt.dao.SkinRepo;
import com.kbeauty.gbt.dao.SkinResourceRepo;
import com.kbeauty.gbt.dao.SkinScoreRepo;
import com.kbeauty.gbt.dao.SkinTypeTestRepo;
import com.kbeauty.gbt.dao.SkinTypeTestStepRepo;
import com.kbeauty.gbt.dao.UserRepo;
import com.kbeauty.gbt.entity.AiRawScore;
import com.kbeauty.gbt.entity.domain.Code;
import com.kbeauty.gbt.entity.domain.CommonDomain;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinDoctor;
import com.kbeauty.gbt.entity.domain.SkinGroup;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.SkinItem;
import com.kbeauty.gbt.entity.domain.SkinLevel;
import com.kbeauty.gbt.entity.domain.SkinRanking;
import com.kbeauty.gbt.entity.domain.SkinResource;
import com.kbeauty.gbt.entity.domain.SkinScore;
import com.kbeauty.gbt.entity.domain.SkinTypeTest;
import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.BstiType;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.SkinArea;
import com.kbeauty.gbt.entity.enums.SkinAreaEditor;
import com.kbeauty.gbt.entity.enums.SkinAreaProd;
import com.kbeauty.gbt.entity.enums.SkinAreaTreat;
import com.kbeauty.gbt.entity.enums.SkinLevelType;
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.entity.enums.UserSkinStatus;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.AiRecommand;
import com.kbeauty.gbt.entity.view.AiRecommandRaw;
import com.kbeauty.gbt.entity.view.AiRecommandView;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.DiaryView;
import com.kbeauty.gbt.entity.view.SkinRank;
import com.kbeauty.gbt.entity.view.SkinRankingView;
import com.kbeauty.gbt.entity.view.SkinResult;
import com.kbeauty.gbt.entity.view.SkinResultView;
import com.kbeauty.gbt.entity.view.SkinTypeView;
import com.kbeauty.gbt.entity.view.UserSkinCondition;
import com.kbeauty.gbt.entity.view.UserSkinListView;
import com.kbeauty.gbt.entity.view.UserSkinView;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.DateUtil;
import com.kbeauty.gbt.util.StringUtil;

@Service
public class SkinService {

	@Autowired
	private SkinGroupRepo skinGroupRepo;

	@Autowired
	private SkinLevelRepo skinLevelRepo;

	@Autowired
	private SkinItemRepo skinItemRepo;

	@Autowired
	private SkinInfoRepo skinInfoRepo;

	@Autowired
	private SkinDoctorRepo skinDoctorRepo;
	
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private SkinMapper skinMapper;

	@Autowired
	private SkinRepo skinRepo;

	@Autowired
	private SkinScoreRepo skinScoreRepo;

	@Autowired
	private SkinResourceRepo skinResourceRepo;

	@Autowired
	private SkinRankingRepo skinRankingRepo;
	
	@Autowired
	private SkinTypeTestRepo skinTypeTestRepo;
	
	@Autowired
	private SkinTypeTestStepRepo skinTypeTestStepRepo;

	@Autowired
	private UserService userService;
	
	 

	@Autowired
	private ContentService contentService;

	public void save(SkinGroup skinGroup) {
		skinGroupRepo.save(skinGroup);
	}

	public void save(SkinLevel skinLevel) {
		skinLevelRepo.save(skinLevel);
	}

	public void save(SkinItem skinItem) {
		skinItemRepo.save(skinItem);
	}

	public void save(SkinDoctor skinDoctor) {
		skinDoctorRepo.save(skinDoctor);
	}

	// TODO 여기 객체를 캐쉬해야 함.
	public AiRecommandView getAiRecommandView() {
		AiRecommandView view = new AiRecommandView();
		List<AiRecommandRaw> aiRecommandRawList = skinMapper.getAiRecommandList();
		view.setAiRecommandRawList(aiRecommandRawList);
		// 여기서 데이터 설정 XXXXX

		view.generate();

		return view;
	}

	public SkinResultView getSkinResultView(AiRawScore score, String userId) {
		SkinResultView view = new SkinResultView();
		AiRecommandView aiView = getAiRecommandView();

		// 기본값 조회
		List<Code> codeDataList = getCodeData(Code.SKIN_BASE_SCORE);
		SkinArea[] skinAreas = SkinArea.values();
		SkinResult skinResult = null;

		for (SkinArea skinArea : skinAreas) { // 각 부위별 기본 점수 설정
			for (Code codeData : codeDataList) {
				if (skinArea.getCode().equals(codeData.getNCode())) {
					score.setScore(skinArea, codeData.getIntVal());
				}
			}
		}

		List<SkinResult> skiResultList = getSkiResultList(aiView, score);
		view.setSkinResultList(skiResultList);
		view.generateData();
		int totalScore = view.getTotalScore();
		String skinComment = getSkinComment(aiView, totalScore);
		view.setSkinComment(skinComment);

		AiRecommand aiRecommand = aiView.getAiRecommandList().get(0);
		view.setGroupId(aiRecommand.getGroupId());

		// 피부 랭킹
		// 피부 나이
		setSkinRank(view, score, userId);

		return view;
	}

	public List<Code> getCodeData(String code) {
		Code inputCode = new Code();
		inputCode.setMCode(code);
		List<Code> codeDataList = skinMapper.getCodeData(inputCode); // 기본 점수 조회
		return codeDataList;
	}

	/**
	 * 피부 랭킹 , 피부 나이 설정
	 * 
	 * @param view
	 * @param score
	 */

	private void setSkinRank(SkinResultView view, AiRawScore score, String userId) {
		User user = userService.getUser(userId);

		String birthDay = user.getBirthDay();
		if (StringUtil.isEmpty(birthDay) || birthDay.length() != 8) {
			return;
		}

		String[] betweenAgeDate = DateUtil.getBetweenAgeDate(birthDay);

		SkinRank inputRank = new SkinRank();
		inputRank.setInputScore(score.getSkinRankData());
		inputRank.setStartDate(betweenAgeDate[0]);
		inputRank.setEndDate(betweenAgeDate[1]);

		SkinRank skinRank = skinMapper.getSkinRank(inputRank);
		int ranking = skinRank.getRanking();
		float rankingPercent = 0f;

		try {
			int count = skinRank.getCnt();
			rankingPercent = ranking / (float) count * 1000 - 0.5f; // 버림 처리
			int rankingPercentRound = Math.round(rankingPercent);

			//
			int mod = rankingPercentRound % 10;
			// 소수점 보이지 않게 하기 위해서, 강제로 0 처리
			mod = 0;

			int min = 580;
			if (rankingPercentRound >= min) {
				rankingPercentRound = min + mod;
			}

			rankingPercent = rankingPercentRound / 10;

		} catch (Exception re) {
			rankingPercent = -1f;
		}

		List<Code> codeDataList = getCodeData(Code.SKIN_AGE_WEIGHT);

//		inputRank.setInputScore(score.getSkinAgeData01());		
		int adjustTotalScore = score.getSkinAgeData01(codeDataList);
		inputRank.setInputScore(adjustTotalScore);
		skinRank = skinMapper.getSkinAge01(inputRank);

		int skinAge01 = skinMapper.getSkinAge(adjustTotalScore);

//		int skinAge01 = AiRawScore.getSkinAge(skinRank);

// 미사용 		
//		inputRank.setInputScore(score.getSkinAgeData02());		
//		skinRank = skinMapper.getSkinAge02(inputRank);		
//		int skinAge02 = AiRawScore.getSkinAge(skinRank);

		view.setSkinRank(ranking);
		view.setSkinAge01(skinAge01);
//		view.setSkinAge02(skinAge02);
		view.setSkinRankPercent((int) rankingPercent);
	}

	public String getShareLink(String skinId) { //공유하기 링크생성
		String str = "https://prd.beautej.com/w1/skin_share/" + skinId;
		return str;
	}

	private String getSkinComment(AiRecommandView view, int totalScore) {
		StringBuffer sb = new StringBuffer();
		List<AiRecommand> aiRecommandList = view.getAiRecommandList();
		String fixCode = "10170"; // 총평 고정 Comment
		String ranCode = "10180"; // 총평 random Comment
		List<String> randomCommentList = new ArrayList<String>();

		for (AiRecommand aiRecommand : aiRecommandList) {
			if (totalScore >= aiRecommand.getFromVal() && totalScore <= aiRecommand.getToVal()) {

				if (fixCode.equals(aiRecommand.getItemCode())) { // 고정 셋팅
					sb.append(aiRecommand.getContent());
				}

				if (ranCode.equals(aiRecommand.getItemCode())) { // 랜덤 셋팅
					randomCommentList.add(aiRecommand.getContent());
				}
			}
		}

		int randomInt = CommonUtil.getRandomInt(randomCommentList.size());
		String randomComment = randomCommentList.get(randomInt);
		if (!StringUtil.isEmpty(randomComment)) {
			sb.append(" ").append(randomComment);
		}

		return sb.toString();
	}

	private List<SkinResult> getSkiResultList(AiRecommandView aiView, AiRawScore score) {
		List<SkinResult> list = new ArrayList<>();

		SkinArea[] skinAreas = SkinArea.values();
		SkinResult skinResult = null;
		for (SkinArea skinArea : skinAreas) {
			skinResult = getSkinResult(aiView, score, skinArea);

			setAiProduct(aiView, skinArea.getSkinAreaProd(), skinResult, score);

			setAiTreat(aiView, skinArea.getSkinAreaTreat(), skinResult, score);

			setAiEditor(aiView, skinArea.getSkinAreaEditor(), skinResult, score);

			list.add(skinResult);
		}

		return list;
	}

	private SkinResult getSkinResult(AiRecommandView view, AiRawScore score, SkinArea skinArea) {
		int skinScore = score.getScore(skinArea);
		SkinResult result = new SkinResult();
		List<AiRecommand> aiRecommandList = view.getAiRecommandList();
		for (AiRecommand aiRecommand : aiRecommandList) {
			if (skinArea.getCode().equals(aiRecommand.getItemCode())) {
				if (skinScore >= aiRecommand.getFromVal() && skinScore <= aiRecommand.getToVal()) {

					result.setScore(skinScore);
					result.setOrgScore(score.getOrgScore(skinArea));
					result.setItemCode(aiRecommand.getItemCode());
					result.setComment(aiRecommand.getContent());
				}
			}
		}

		return result;
	}

	private SkinResult setAiComment(AiRecommandView view, AiRawScore score, SkinArea skinArea, SkinResult result) {
		int skinScore = score.getScore(skinArea);
		List<AiRecommand> aiRecommandList = view.getAiRecommandList();
		for (AiRecommand aiRecommand : aiRecommandList) {
			if (skinArea.getCode().equals(aiRecommand.getItemCode())) {
				if (skinScore >= aiRecommand.getFromVal() && skinScore <= aiRecommand.getToVal()) {

					result.setScore(skinScore);
					result.setOrgScore(score.getOrgScore(skinArea));
					result.setItemCode(aiRecommand.getItemCode());
					result.setComment(aiRecommand.getContent());
				}
			}
		}

		return result;
	}

	private void setAiProduct(AiRecommandView view, SkinAreaProd skinArea, SkinResult result, AiRawScore score) {
		int skinScore = result.getScore();
		List<AiRecommand> aiRecommandList = view.getAiRecommandProdList();
		for (AiRecommand aiRecommand : aiRecommandList) {
			if (skinArea.getCode().equals(aiRecommand.getItemCode())) {
				if (skinScore >= aiRecommand.getFromVal() && skinScore <= aiRecommand.getToVal()) {
					String contentId = aiRecommand.getRefId();
					ContentView contentView = contentService.getContentView(contentId);
					int count = aiRecommand.getPickCount();
					contentView.setShuffleResoureList(count);
					result.setProdContentView(contentView);
				}
			}
		}
	}

	private void setAiTreat(AiRecommandView view, SkinAreaTreat skinArea, SkinResult result, AiRawScore score) {
		int skinScore = result.getScore();
		List<AiRecommand> aiRecommandList = view.getAiRecommandTreatList();
		for (AiRecommand aiRecommand : aiRecommandList) {
			if (skinArea.getCode().equals(aiRecommand.getItemCode())) {
				if (skinScore >= aiRecommand.getFromVal() && skinScore <= aiRecommand.getToVal()) {
					String contentId = aiRecommand.getRefId();
					ContentView contentView = contentService.getContentView(contentId);
					int count = aiRecommand.getPickCount();
					contentView.setShuffleResoureList(count);
					result.setTreatContentView(contentView);
				}
			}
		}
	}

	private void setAiEditor(AiRecommandView view, SkinAreaEditor skinArea, SkinResult result, AiRawScore score) {
		int skinScore = result.getScore();
		List<AiRecommand> aiRecommandList = view.getAiRecommandEditorList();
		for (AiRecommand aiRecommand : aiRecommandList) {
			if (skinArea.getCode().equals(aiRecommand.getItemCode())) {
				if (skinScore >= aiRecommand.getFromVal() && skinScore <= aiRecommand.getToVal()) {

					if (SkinLevelType.BOTTOM.getCode().equals(aiRecommand.getLevelType())) {
						int pickCount = aiRecommand.getPickCount(); // 하위 몇개

						if (isInclude(score, result.getOrgScore(), pickCount)) {
							String contentId = aiRecommand.getRefId();
							ContentView contentView = contentService.getContentView(contentId);
							result.setEditorContentView(contentView);
						}
					}
				}
			}
		}
	}

	private boolean isInclude(AiRawScore rawScore, double score, int level) {
//		rawScore 점수를 정렬한다. 
//		level로 해당 정렬에 값을 날린다. 2 ==> 하위 2개만 남기고 나머지 데이터 날린다. 
//		남은 데이터에 값에 score 가 있으면 참 / 없으면 false

		boolean isTrue = false;
		DoubleStream builderStream = DoubleStream.builder().add(rawScore.getRedness()).add(rawScore.getPore())
				.add(rawScore.getPigment()).add(rawScore.getTrouble()).add(rawScore.getWrinkle()).build();

		Set<Double> collect = builderStream.sorted().limit(level).boxed().collect(Collectors.toSet());
		boolean contains = collect.contains(score);

		return contains;
	}

	public List<SkinInfo> saveSkinInfo(VisionFace visionFace, String imgId, CommonDomain basicInfo) {
		List<SkinInfo> list = new ArrayList<SkinInfo>();
		// 1000:얼굴정보/2000:landmark/3000:기타정보
		Dimension dimension = visionFace.getDimension();

		SkinInfo faceInfo = new SkinInfo(dimension, imgId); // 11000 FacePosition
		faceInfo.setBasicInfo(basicInfo);
		list.add(faceInfo);

		List<NeoLandmark> landmarks = visionFace.getLandmarks();
		Set<String> keys = new HashSet<>();
		for (NeoLandmark neoLandmark : landmarks) {
			if (keys.contains(neoLandmark.getType())) {
				continue;
			} else {
				keys.add(neoLandmark.getType());
			}

			faceInfo = new SkinInfo(neoLandmark, imgId); // 11000 FacePosition
			faceInfo.setBasicInfo(basicInfo);
			list.add(faceInfo);
		}

		faceInfo = new SkinInfo(visionFace.getPose(), imgId); // 11000 FacePosition
		faceInfo.setBasicInfo(basicInfo);
		list.add(faceInfo);

		for (SkinInfo skinInfo : list) {
			skinInfoRepo.save(skinInfo);
		}

		return list;
	}

	public void saveSkinResultView(SkinResultView view, CommonDomain basicInfo) {
		Skin skin = new Skin();
		skin.setSkinId(view.getImgId());
		skin.setUserId(view.getUserId());
		skin.setAiUrl(view.getImgUrl());
		skin.setGroupId(view.getGroupId());
		skin.setComment(view.getSkinComment());
		skin.setDDay(view.getProcessDate());
		skin.setStatus(UserSkinStatus.REG.getCode());

		skin.setDir(view.getUserName()); // 홈페이지 전용 필드

		skin.setBasicInfo(basicInfo);

		skinRepo.save(skin);

		List<SkinResult> skinResultList = view.getSkinResultList();
		SkinScore score = null;
		for (SkinResult skinResult : skinResultList) {
			score = new SkinScore();
			score.setSkinId(skin.getSkinId());
			score.setItemCode(skinResult.getItemCode());
			score.setScore(skinResult.getOrgScore()); // AI 점수
			score.setAdjustScore(skinResult.getScore()); // 조정 점수
			score.setComment(skinResult.getComment());
			score.setOwnerType("A");
			score.setBasicInfo(basicInfo);
			skinScoreRepo.save(score);

			List<SkinResource> skinResourceProd = getSkinResource(score, skinResult.getProdContentView());
			for (SkinResource skinResource : skinResourceProd) {
				skinResource.setBasicInfo(basicInfo);
				skinResourceRepo.save(skinResource);
			}

			List<SkinResource> skinResourceTreat = getSkinResource(score, skinResult.getTreatContentView());
			for (SkinResource skinResource : skinResourceTreat) {
				skinResource.setBasicInfo(basicInfo);
				skinResourceRepo.save(skinResource);
			}

			List<SkinResource> skinResourceEditor = getSkinResource(score, skinResult.getEditorContentView());
			for (SkinResource skinResource : skinResourceEditor) {
				skinResource.setBasicInfo(basicInfo);
				skinResourceRepo.save(skinResource);
			}

		}
		saveSkinRanking(view, basicInfo);
	}
	public void saveSkinRanking(SkinResultView view, CommonDomain basicInfo) {
		User user = userRepo.findByUserId(view.getUserId());
		if (user.getUserRole().equals(UserRole.HOMEPAGE.getCode())||user==null)
			return;
		UserSkinView userSkinView = getUserSkinView(view.getImgId());
		SkinRanking rank = null;
		rank = skinRankingRepo.findByUserId(view.getUserId());

		if (rank == null) {
			rank = new SkinRanking();
		}
		rank.setSex(userSkinView.getSex());
		rank.setUserId(view.getUserId()); // 사용자 아이디
		rank.setSkinId(view.getImgId()); // 스킨 아이디
		rank.setScore(view.getDecimalTotal()); // 점수 XX.X
		rank.setBasicInfo(basicInfo); //
		rank.setAge(userSkinView.getManAge());

		skinRankingRepo.save(rank);
	}
	
	public Skin saveSkinDoctor(SkinDoctor skin) {

		if (!skin.isValidScore() || skin.isEmpty()) {
			return null;
		} else {
			skinDoctorRepo.save(skin);

			updateSkinStatusCOM(skin.getSkinId());

			return skinRepo.findBySkinId(skin.getSkinId());
		}
	}

	private List<SkinResource> getSkinResource(SkinScore score, ContentView view) {
		List<SkinResource> skinResourceList = new ArrayList<>();
		if (view == null) {
			return skinResourceList;
		}

		Content content = view.getContent();

		List<Resources> resourceList = view.getResourceList();

		SkinResource skinResource = null;	
		for (Resources resources : resourceList) {
			skinResource = new SkinResource();
			skinResource.setSkinId(score.getSkinId());
			skinResource.setItemCode(score.getItemCode());
			skinResource.setContentId(content.getContentId());
			skinResource.setContentType(content.getContentType());
			skinResource.setResourceId(resources.getResourceId());

			skinResourceList.add(skinResource);
		}

		return skinResourceList;
	}

	private Map<String, String> getUserMap(List<UserSkinListView> userSkinList) {
		List<String> userids = new ArrayList<String>();
		for (UserSkinListView item : userSkinList) {
			userids.add(item.getUserId());
		}

		Map<String, String> userMap = userService.getUserMap(userids);
		return userMap;
	}

	public List<UserSkinListView> getUserSkinList(UserSkinCondition condition) {
		List<UserSkinListView> userSkinList = skinMapper.getUserSkinList(condition);

		Map<String, String> userMap = getUserMap(userSkinList);
		String userName = null;
		for (UserSkinListView userSkinListView : userSkinList) {
			int totalScore = 0;
			double decimalTotal = 0;
			userName = userMap.get(userSkinListView.getUserId());
			userSkinListView.setUserName(userName);

//			10000	주름
//			10010	모공
//			10020	Trouble
//			10030	Pigment
//			10040	Redness

			List<SkinScore> skinScoreList = skinScoreRepo.findBySkinId(userSkinListView.getSkinid());
			for (SkinScore skinScore : skinScoreList) {
				int adjustScore = skinScore.getAdjustScore();
				totalScore += adjustScore;
				if (SkinArea.WRINKLE.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setWrinkleAiScore(skinScore.getScore());
					userSkinListView.setWrinkleScore(adjustScore);
				} else if (SkinArea.PORE.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setPoreAiScore(skinScore.getScore());
					userSkinListView.setPoreScore(adjustScore);
				} else if (SkinArea.TROUBLE.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setTroubleAiScore(skinScore.getScore());
					userSkinListView.setTroubleScore(adjustScore);
				} else if (SkinArea.PIGMENT.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setPigmentAiScore(skinScore.getScore());
					userSkinListView.setPigmentScore(adjustScore);
				} else if (SkinArea.REDNESS.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setRednessAiScore(skinScore.getScore());
					userSkinListView.setRednessScore(adjustScore);
				}
			}
			decimalTotal = (double) totalScore / 5.0;
			decimalTotal = (double) (Math.round(decimalTotal * 10) / 10.0);
			totalScore = (int) Math.round(totalScore / 5);
			userSkinListView.setDecimalTotal(decimalTotal);
			userSkinListView.setTotalScore(totalScore);
		}

		return userSkinList;
	}

	public List<UserSkinListView> getREQSkinList(UserSkinCondition condition) {
		List<UserSkinListView> userSkinList = skinMapper.getREQSkinList(condition);

		Map<String, String> userMap = getUserMap(userSkinList);
		String userName = null;
		for (UserSkinListView userSkinListView : userSkinList) {
			int totalScore = 0;
			double decimalTotal = 0;
			userName = userMap.get(userSkinListView.getUserId());
			userSkinListView.setUserName(userName);

//			10000	주름
//			10010	모공
//			10020	Trouble
//			10030	Pigment
//			10040	Redness

			List<SkinScore> skinScoreList = skinScoreRepo.findBySkinId(userSkinListView.getSkinid());
			for (SkinScore skinScore : skinScoreList) {
				int adjustScore = skinScore.getAdjustScore();
				totalScore += adjustScore;
				if (SkinArea.WRINKLE.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setWrinkleAiScore(skinScore.getScore());
					userSkinListView.setWrinkleScore(adjustScore);
				} else if (SkinArea.PORE.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setPoreAiScore(skinScore.getScore());
					userSkinListView.setPoreScore(adjustScore);
				} else if (SkinArea.TROUBLE.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setTroubleAiScore(skinScore.getScore());
					userSkinListView.setTroubleScore(adjustScore);
				} else if (SkinArea.PIGMENT.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setPigmentAiScore(skinScore.getScore());
					userSkinListView.setPigmentScore(adjustScore);
				} else if (SkinArea.REDNESS.getCode().equals(skinScore.getItemCode())) {
					userSkinListView.setRednessAiScore(skinScore.getScore());
					userSkinListView.setRednessScore(adjustScore);
				}
			}
			decimalTotal = (double) totalScore / 5.0;
			decimalTotal = (double) (Math.round(decimalTotal * 10) / 10.0);
			totalScore = (int) Math.round(totalScore / 5);
			userSkinListView.setDecimalTotal(decimalTotal);
			userSkinListView.setTotalScore(totalScore);
		}

		return userSkinList;
	}

	public int getUserSkinListCnt(UserSkinCondition condition) {
		return skinMapper.getUserSkinListCnt(condition);
	}

	public int getREQSkinListCnt(UserSkinCondition condition) {
		return skinMapper.getREQSkinListCnt(condition);
	}

	public UserSkinView getUserSkinView(String skinId) {
		return getUserSkinView(skinId, null);
	}

	public UserSkinView getUserSkinView(String skinId, String loginUserId) {
		UserSkinView view = new UserSkinView();
		Skin skin = skinRepo.findBySkinId(skinId);
		User user = userService.getUser(skin.getUserId());
		skin.setUserName(user.getUserName());

		List<SkinScore> skinScoreList = skinScoreRepo.findBySkinId(skinId);
		skinScoreList = sortViewScoreList(skinScoreList);
		int totalScore = 0;
		double decimalTotal = 0;
		for (SkinScore skinScore : skinScoreList) {
			int adjustScore = skinScore.getAdjustScore();
			totalScore += adjustScore;
		}

		AiRawScore aiRawScore = genAiRawScore(skinScoreList);
		SkinResultView inputView = new SkinResultView();
		setSkinRank(inputView, aiRawScore, user.getUserId());

		view.setSkinRank(inputView.getSkinRank());
		view.setSkinAge01(inputView.getSkinAge01());
		view.setSkinAge02(inputView.getSkinAge02());
		view.setSkinRankPercent(inputView.getSkinRankPercent());

		String shareLink = getShareLink(skinId);
		view.setShareLink(shareLink);

		int manAge = 0;

		String birthDay = user.getBirthDay();
		if (!StringUtil.isEmpty(birthDay)) {
			manAge = DateUtil.getManAge(birthDay);
		}

		if (user.isHomepageUser()) {
			manAge = 0;
		}

		view.setManAge(manAge);

		decimalTotal = (double) totalScore / 5.0;
		decimalTotal = (double) (Math.round(decimalTotal * 10) / 10.0);
		totalScore = (int) Math.round(totalScore / 5);
		skin.setTotalScore(totalScore);
		skin.setDecimalTotal(decimalTotal);
		view.setSex(user.getSex());

		view.setSkin(skin);
		view.setSkinScoreList(skinScoreList);
		return view;
	}

	private AiRawScore genAiRawScore(List<SkinScore> skinScoreList) {
		AiRawScore aiRawScore = new AiRawScore();

		for (SkinScore skinScore : skinScoreList) {
			if (SkinArea.WRINKLE.getCode().equals(skinScore.getItemCode())) {
				aiRawScore.setCWrinkle(skinScore.getAdjustScore());
				aiRawScore.setWrinkle(skinScore.getScore());
			} else if (SkinArea.PORE.getCode().equals(skinScore.getItemCode())) {
				aiRawScore.setCPore(skinScore.getAdjustScore());
				aiRawScore.setPore(skinScore.getScore());
			} else if (SkinArea.TROUBLE.getCode().equals(skinScore.getItemCode())) {
				aiRawScore.setCTrouble(skinScore.getAdjustScore());
				aiRawScore.setTrouble(skinScore.getScore());
			} else if (SkinArea.PIGMENT.getCode().equals(skinScore.getItemCode())) {
				aiRawScore.setCPigment(skinScore.getAdjustScore());
				aiRawScore.setPigment(skinScore.getScore());
			} else if (SkinArea.REDNESS.getCode().equals(skinScore.getItemCode())) {
				aiRawScore.setCRedness(skinScore.getAdjustScore());
				aiRawScore.setRedness(skinScore.getScore());
			}
		}

		return aiRawScore;
	}

	public void deleteUserSkin(String skinId, String userId) {
		Skin skin = skinRepo.findBySkinId(skinId);
		skin.setDelete();
		skin.setBasicInfo(CommonUtil.getSysTime(), userId);

		skinRepo.save(skin);
	}

	public List<DiaryView> getMonthSkinData(UserSkinCondition condition) {
		String[] betweenDate = DateUtil.getBetweenDate(condition.getDDay());
		// condition.setUserId(userId);
		condition.setStartDate(betweenDate[0]);
		condition.setEndDate(betweenDate[1]);	
		List<DiaryView> monthSkinData = skinMapper.getMonthSkinData(condition);
		return monthSkinData;
	}

	public DiaryView getDailySkinData(UserSkinCondition condition) {
		DiaryView dailySkinData = skinMapper.getDailySkinData(condition);
		String skinId = dailySkinData.getSkinId();

		SkinResultView view = getSkinResultViewBySkinId(skinId);
		view.generateData();

		dailySkinData.add(view);

		return dailySkinData;
	}

	public DiaryView getTermDailySkinData(UserSkinCondition condition) {
		DiaryView dailySkinData = new DiaryView();
		List<Skin> skinList = skinMapper.getTermDailySkinData(condition);
		String skinId;
		SkinResultView view;
		for (Skin skin : skinList) {
			skinId = skin.getSkinId();
			view = getSkinResultViewBySkinId(skinId, false);
			view.generateData();
			dailySkinData.add(view);
		}
		return dailySkinData;
	}

	public SkinResultView getSkinResultViewBySkinId(String skinId) {
		return getSkinResultViewBySkinId(skinId, true);
	}

	public SkinResultView getSkinResultViewBySkinId(String skinId, boolean flag) {
		Skin skin = skinRepo.findBySkinId(skinId);
		List<SkinScore> skinScoreList = skinScoreRepo.findBySkinId(skinId);
		List<SkinResource> skinResourceList = skinResourceRepo.findBySkinId(skinId); // SKINRESOURCE 를 SKINID로 가져옴
		Map<String, ContentView> prodContentViewMap = new HashMap<>();
		Map<String, ContentView> treatContentViewMap = new HashMap<>();
		Map<String, ContentView> editorContentViewMap = new HashMap<>();

		Map<String, Set<String>> prodContentViewSet = new HashMap<>();
		Map<String, Set<String>> treatContentViewSet = new HashMap<>();
		Map<String, Set<String>> editorContentViewSet = new HashMap<>();
		Set<String> prodResourceSet = null;
		Set<String> treatResourceSet = null;
		Set<String> editorResourceSet = null;
		if (flag) {
			for (SkinResource skinResource : skinResourceList) { // RESOURCE들 들고 반복
				if (isProduct(skinResource.getContentType())) { // 9040 AI시술추천제품인지 물어봄
					prodResourceSet = prodContentViewSet.get(skinResource.getItemCode()); // 잠시동안 SET을 주름,홍조등 각 영역의
																							// set으로 바꿈
					if (prodResourceSet == null) { // 아직 없다면
						prodResourceSet = new HashSet<>();
						prodContentViewSet.put(skinResource.getItemCode(), prodResourceSet); // 만들어줌
					}
					prodResourceSet.add(skinResource.getResourceId()); // RESOURCE ID 를 SET에 넣음
				} else if (isTreat(skinResource.getContentType())) {
					treatResourceSet = treatContentViewSet.get(skinResource.getItemCode());
					if (treatResourceSet == null) {
						treatResourceSet = new HashSet<>();
						treatContentViewSet.put(skinResource.getItemCode(), treatResourceSet);
					}
					treatResourceSet.add(skinResource.getResourceId());
				} else if (isEditor(skinResource.getContentType())) {
					editorResourceSet = editorContentViewSet.get(skinResource.getItemCode());
					if (editorResourceSet == null) {
						editorResourceSet = new HashSet<>();
						editorContentViewSet.put(skinResource.getItemCode(), editorResourceSet);
					}
					editorResourceSet.add(skinResource.getResourceId());
				}
			}
		}
		ContentView tempContentView = null;
		ContentView contentView = null;
		// contentID로 ContentView 조회
		List<Resources> tempResourceList = null;
		if (flag) {
			for (SkinResource skinResource : skinResourceList) { // 해당 스킨 ID들의 리소스 SKINRESOURCES
				if (isProduct(skinResource.getContentType())) { // 부위별 다섯번 ( 리소스의 TYPE이 1040이면)
					tempContentView = prodContentViewMap.get(skinResource.getItemCode()); // resource id 로 이루어진 셋
					if (tempContentView == null) {
//					contentView = contentService.getContentView(skinResource.getContentId()); <== 지워진 Resource들 가져오지 못함
						contentView = contentService.getContentViewAllResource(skinResource.getContentId()); // contentid 로 리소스를 끌어오기위해 컨텐츠를 찾음
						tempResourceList = new ArrayList<Resources>();
						List<Resources> resourceList = contentView.getResourceList();
						prodResourceSet = prodContentViewSet.get(skinResource.getItemCode());
						for (Resources resource : resourceList) {
							if (prodResourceSet.contains(resource.getResourceId())) {
								tempResourceList.add(resource);
							}
						}
						contentView.setResourceList(tempResourceList);

						prodContentViewMap.put(skinResource.getItemCode(), contentView);
					}
				} else if (isTreat(skinResource.getContentType())) {
					tempContentView = treatContentViewMap.get(skinResource.getItemCode());
					if (tempContentView == null) {
						contentView = contentService.getContentView(skinResource.getContentId());

						tempResourceList = new ArrayList<Resources>();
						List<Resources> resourceList = contentView.getResourceList();
						treatResourceSet = treatContentViewSet.get(skinResource.getItemCode());
						for (Resources resource : resourceList) {
							if (treatResourceSet.contains(resource.getResourceId())) {
								tempResourceList.add(resource);
							}
						}
						contentView.setResourceList(tempResourceList);

						treatContentViewMap.put(skinResource.getItemCode(), contentView);
					}
				} else if (isEditor(skinResource.getContentType())) {
					tempContentView = editorContentViewMap.get(skinResource.getItemCode());
					if (tempContentView == null) {
						contentView = contentService.getContentView(skinResource.getContentId());

						tempResourceList = new ArrayList<Resources>();
						List<Resources> resourceList = contentView.getResourceList();
						editorResourceSet = editorContentViewSet.get(skinResource.getItemCode());
						for (Resources resource : resourceList) {
							if (editorResourceSet.contains(resource.getResourceId())) {
								tempResourceList.add(resource);
							}
						}
						contentView.setResourceList(tempResourceList);

						editorContentViewMap.put(skinResource.getItemCode(), contentView);
					}
				}
			}
		}

		SkinResultView view = new SkinResultView();
		view.setImgId(skin.getSkinId());
		view.setUserId(skin.getUserId());
		view.setImgUrl(skin.getAiUrl());
		String url1 = skin.getAiUrl().substring(0,96);
		String url2 = skin.getAiUrl().substring(98);
		view.setTTUrl(url1+"TT_"+url2);
		view.setTBUrl(url1+"TB_"+url2);
		view.setULUrl(url1+"UL_"+url2);
		view.setURUrl(url1+"UR_"+url2);
		view.setFLUrl(url1+"FL_"+url2);
		view.setFMUrl(url1+"FM_"+url2);
		view.setFRUrl(url1+"FR_"+url2);
		view.setGLUrl(url1+"GL_"+url2);
		view.setELUrl(url1+"EL_"+url2);
		view.setERUrl(url1+"ER_"+url2);
		view.setCRUrl(url1+"CR_"+url2);
		view.setCLUrl(url1+"CL_"+url2);
		view.setMLUrl(url1+"ML_"+url2);
		view.setMOUrl(url1+"MO_"+url2);
		view.setMRUrl(url1+"MR_"+url2);									
		view.setGroupId(skin.getGroupId());
		view.setSkinComment(skin.getComment());
		view.setProcessDate(skin.getDDay());

		List<SkinResult> skinResultList = new ArrayList<SkinResult>();
		SkinResult skinResult = null;

		for (SkinScore skinScore : skinScoreList) {
			skinResult = new SkinResult();
			skinResult.setItemCode(skinScore.getItemCode());
			skinResult.setOrgScore(skinScore.getScore());
			skinResult.setScore(skinScore.getAdjustScore());
			skinResult.setComment(skinScore.getComment());

			tempContentView = prodContentViewMap.get(skinScore.getItemCode());
			if (tempContentView != null) {
				skinResult.setProdContentView(tempContentView);
			}

			tempContentView = treatContentViewMap.get(skinScore.getItemCode());
			if (tempContentView != null) {
				skinResult.setTreatContentView(tempContentView);
			}

			tempContentView = editorContentViewMap.get(skinScore.getItemCode());
			if (tempContentView != null) {
				skinResult.setEditorContentView(tempContentView);
			}

			skinResultList.add(skinResult);
		}

		AiRawScore aiRawScore = genAiRawScore(skinScoreList);
		setSkinRank(view, aiRawScore, skin.getUserId());

		String shareLink = getShareLink(view.getImgId()); // imgId가 skinId가 된다.
		view.setShareLink(shareLink);

		view.setSkinResultList(skinResultList);
		
		return view;
	}
	
	private List<SkinScore> sortViewScoreList(List<SkinScore> skinScoreList) {
		if (skinScoreList == null)
			return skinScoreList;
		if (skinScoreList.isEmpty())
			return skinScoreList;
		List<SkinScore> sortedSkinScoreList = new ArrayList<SkinScore>(5);
		sortedSkinScoreList.add(null);
		sortedSkinScoreList.add(null);
		sortedSkinScoreList.add(null);
		sortedSkinScoreList.add(null);
		sortedSkinScoreList.add(null);

		// 화면 순서 ==> 주름 색소침착 트러블 모공 홍조
		// 코드
//		10000	주름기본
//		10010	모공기본
//		10020	트러블기본
//		10030	색소침착기본
//		10040	홍조기본
		String itemCode = null;
		for (SkinScore skinScore : skinScoreList) {
			itemCode = skinScore.getItemCode();
			if (SkinArea.WRINKLE.getCode().equals(itemCode)) {
				skinScore.setItemName(SkinArea.WRINKLE.getVal());
				sortedSkinScoreList.set(0, skinScore);
			} else if (SkinArea.PIGMENT.getCode().equals(itemCode)) {
				skinScore.setItemName(SkinArea.PIGMENT.getVal());
				sortedSkinScoreList.set(1, skinScore);
			} else if (SkinArea.TROUBLE.getCode().equals(itemCode)) {
				skinScore.setItemName(SkinArea.TROUBLE.getVal());
				sortedSkinScoreList.set(2, skinScore);
			} else if (SkinArea.PORE.getCode().equals(itemCode)) {
				skinScore.setItemName(SkinArea.PORE.getVal());
				sortedSkinScoreList.set(3, skinScore);
			} else if (SkinArea.REDNESS.getCode().equals(itemCode)) {
				skinScore.setItemName(SkinArea.REDNESS.getVal());
				sortedSkinScoreList.set(4, skinScore);
			}
		}

		return sortedSkinScoreList;
	}
	
	public List<Skin> getSimpleSkin(String userId){
		List<Skin> skinList = new ArrayList<Skin>();
		skinList = skinMapper.getFiveDaySkin(userId);
		
		return skinList;
	}
	
	public boolean updateStatusWait(String skinId) {
		try {
			Skin updateSkin = skinRepo.findBySkinId(skinId);
			updateSkin.setStatus(UserSkinStatus.REQ.getCode());

			Skin save = skinRepo.save(updateSkin);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void updateSkinStatusCOM(String skinId) {
		Skin updateSkin = skinRepo.findBySkinId(skinId);
		updateSkin.setStatus(UserSkinStatus.COM.getCode());

		Skin save = skinRepo.save(updateSkin);
	}

	public List<SkinRanking> getSkinRankingBasic(SkinRankingView view) {
		return skinMapper.getSkinRankingTop10(view);
	}

	public SkinDoctor getMySkinDoctor(SkinDoctor skin_user) {
		return skinMapper.getMySkinDoctorScore(skin_user);
	}

	public boolean doctorOrAdmin(String userId) {
		User user = userService.getUser(userId);
		return user.getUserRole().equals(UserRole.DOCTOR.getCode());
	}

	public SkinDoctor getAVGScore(String skinId) {
		return skinMapper.getAVGScore(skinId);
	}
	
	public SkinTypeView getSkinTypeTest(String userId){
		SkinTypeView view = new SkinTypeView();
		SkinTypeTest progressTest;
		User user = userRepo.findByUserId(userId);
		if(user==null) {
			view.setError(ErrMsg.NO_USER);
			return view;
		}
		view.setUser(user);
		 List<SkinTypeTest> list = skinMapper.getProgressTestList(userId); // 끝나진 않았지만 진행중인 테스트
			if(list.size()>0) {
				progressTest = list.get(0);	
				List<SkinTypeTestStep> stepList = skinMapper.findByTestId(progressTest.getTestId());
//				List<SkinTypeTestStep> stepList = skinTypeTestStepRepo.findByTestId(progressTest.getTestId());
				view.setSkinTypeTest(progressTest);
				view.setStepList(stepList);
				int finalStep = skinMapper.getSkinTestMaxStep(progressTest.getTestId());
				view.setFinalStep(finalStep);
				view.setStatus("1000");
				view.setOk();
				return view;
			}else {
				view.setStatus("0000");
				view.setOk();
				return view;
			}
	}
	
	public SkinTypeTestStep saveSkinTypeTestStep(SkinTypeTestStep step, String userId) {
		
//		SkinTypeTestStep oldstep = skinMapper.findByTestIdAndStep(step);
		SkinTypeTestStep oldstep = skinTypeTestStepRepo.findByTestIdAndStep(step.getTestId(),step.getStep());
		if (oldstep != null) {
			step.setSeq(oldstep.getSeq());
		}
		String stepId =  CommonUtil.getGuid();
		switch(step.getChoice()) {
			case "A": step.setScore(1);		break;
			case "B": step.setScore(2);		break;
			case "C": step.setScore(3); 	break;
			case "D": step.setScore(4);		break;
			case "E": step.setScore(2.5f);	break;
		}
		step.setStepId(stepId);
		step.setBasicInfo(userId);
		skinTypeTestStepRepo.save(step);
		
		return step;
	}
	
	public SkinTypeView saveSkinTypeTestStep2(SkinTypeTestStep step, String userId) {
		
		SkinTypeView view = new SkinTypeView();
		SkinTypeTest test = skinTypeTestRepo.findByTestId(step.getTestId());
		if(test == null) {
			view.setError(ErrMsg.SET_SKIN_TYPE_TEST_STEP_ERROR);
			return view;
		}
		if(step.getStep()>0) {
			SkinTypeTestStep oldstep = skinTypeTestStepRepo.findByTestIdAndStep(step.getTestId(),step.getStep());
			if (oldstep != null) { // 수정이냐 아니냐
				step.setSeq(oldstep.getSeq());  
			}
			String stepId =  CommonUtil.getGuid();
			switch(step.getChoice()) {
				case "A": step.setScore(1);		break;
				case "B": step.setScore(2);		break;
				case "C": step.setScore(3); 	break;
				case "D": step.setScore(4);		break;
				case "E": step.setScore(2.5f);	break;
			}
			step.setStepId(stepId);
			step.setBasicInfo(userId);
			skinTypeTestStepRepo.save(step);
		}
		view.setSkinTypeTest(test);
		List<SkinTypeTestStep> stepList = skinTypeTestStepRepo.findByTestId(step.getTestId());
		User user = userRepo.findByUserId(userId);
		view.setStepList(stepList);
		view.setUser(user);
		
		view.setOk();
		return view;
	}
	
	public SkinTypeTestStep getSkinTypeTestStep(SkinTypeTestStep step, String userId) {
//		SkinTypeTestStep returnStep = skinMapper.findByTestIdAndStep(step);
		SkinTypeTestStep returnStep = skinTypeTestStepRepo.findByTestIdAndStep(step.getTestId(),step.getStep());
		return returnStep;
	}

	public SkinTypeTest createSkinTypeTest(String userId) {
		SkinTypeTest test = new SkinTypeTest();
		String testId =  CommonUtil.getGuid();
		test.setTestId(testId);
		test.setUserId(userId);
		test.setStatus("1000");
		test.setBasicInfo(userId);
		skinTypeTestRepo.save(test);
		return test;
	}
	
	public SkinTypeView getSkinTypeTestResult(SkinTypeTest test,String userId) {
		
		SkinTypeView view = new SkinTypeView();
		test = skinTypeTestRepo.findByTestId(test.getTestId());
		if(test==null) {
			view.setError(ErrMsg.SKIN_TYPE_TEST_RESULT_ERROR);
			return view;
		}
		view = skinMapper.getSkinTypeTestScore(test);
		test.setScore(view.getScore());
		test.setBasicInfo(userId);
		test.setStatus("2000");
		skinTypeTestRepo.save(test);
		view.setSkinTypeTest(test);
		List<SkinTypeTestStep> stepList = skinTypeTestStepRepo.findByTestId(test.getTestId());
		view.setStepList(stepList);
		User user = userRepo.findByUserId(userId);
		StringBuffer sb = new StringBuffer();
		String od,sr,pn,wt; od = "O"; sr = "S"; pn = "R"; wt ="W";
		if(view.getOdScore()<27) od = "D";
		if(view.getSrScore()<27) sr = "R";
		if(view.getPnScore()<29) pn = "N";
		if(view.getWtScore()>40) wt = "T";
		sb.append(od);
		sb.append(sr);
		sb.append(pn);
		sb.append(wt);
		String bsti = sb.toString();
		BstiType[] df = BstiType.values();
		String code = null;
		for (BstiType bstiType : df) {
			code = bstiType.getVal();
			if (code.equals(bsti)){
				code = bstiType.getCode();
				break;
			}
		}
		user.setBsti(code);
		userRepo.save(user);
		view.setUser(user);
		view.setSkinTypeTest(test);
		return view;
		
	}
	
	public SkinTypeTest delAndNewSkinTypeTest(SkinTypeTest test, String userId) {
		delSkinTypeTest(test, userId);
		return createSkinTypeTest(userId); 
	}
	
	public void delSkinTypeTest(SkinTypeTest test, String userId) {
		
		String testId = test.getTestId();
		SkinTypeTest returnTest = skinTypeTestRepo.findByTestId(testId);
		returnTest.setDelYn(YesNo.YES.getVal());
		returnTest.setBasicInfo(userId);
		skinTypeTestRepo.save(returnTest);
	}

	public void deleteSkinTypeTestAll(String userId) {
		List<SkinTypeTest> testList = skinTypeTestRepo.findByUserId(userId);
		User user =  userRepo.findByUserId(userId);
		user.setBsti("");
		userRepo.save(user);
		for (SkinTypeTest skinTypeTest : testList) {
			skinTypeTest.setDelYn(YesNo.YES.getVal());
			skinTypeTestRepo.save(skinTypeTest);
		}
	}

	public SkinTypeTest getTestByTestId(String testId) {
		return skinTypeTestRepo.findByTestId(testId);
	}
	
	// TODO 아래 함수 공통으로 빼야 함. AiRecommandView 클래스 내부에도 있음

	private boolean isProduct(String contentType) {
		return ContentType.AI_PRODUCT.getCode().equals(contentType);
	}

	private boolean isTreat(String contentType) {
		return ContentType.AI_TREAT.getCode().equals(contentType);
	}

	private boolean isEditor(String itemCode) {
		return ContentType.AI_EXPERT_TIP.getCode().equals(itemCode);
	}

	public List<SkinGroup> getSkinGroupList(UserSkinCondition condition) {
		return skinMapper.getSkinGroupList(condition);
	}

	public int getSkinGroupListCnt(UserSkinCondition condition) {
		return skinMapper.getSkinGroupListCnt(condition);
	}
	
}
