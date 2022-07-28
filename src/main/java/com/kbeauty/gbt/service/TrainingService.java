package com.kbeauty.gbt.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.hibernate.hql.internal.HolderInstantiator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.kbeauty.gbt.ai.FaceEnum;
import com.kbeauty.gbt.ai.GoogleFaceBuilder;
import com.kbeauty.gbt.ai.ImgCrop;
import com.kbeauty.gbt.ai.VisionFace;
import com.kbeauty.gbt.dao.EvaluateRepo;
import com.kbeauty.gbt.dao.LessonMapper;
import com.kbeauty.gbt.dao.LessonRepo;
import com.kbeauty.gbt.dao.ResourceRepo;
import com.kbeauty.gbt.dao.TrainingRepo;
import com.kbeauty.gbt.dao.UserFaceRepo;
import com.kbeauty.gbt.dao.UserRepo;
import com.kbeauty.gbt.entity.AiRawScore;
import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.CommonDomain;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Evaluate;
import com.kbeauty.gbt.entity.domain.FileInfo;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.enums.ContentActive;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FacialContour;
import com.kbeauty.gbt.entity.enums.Orientation;
import com.kbeauty.gbt.entity.enums.SeasonColor;
import com.kbeauty.gbt.entity.enums.SkinTone;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.entity.enums.TrainingStatus;
import com.kbeauty.gbt.entity.enums.UserFaceStatus;
import com.kbeauty.gbt.entity.enums.UserFaceWho;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.SkinResultView;
import com.kbeauty.gbt.entity.view.TrainingCondition;
import com.kbeauty.gbt.entity.view.TrainingView;
import com.kbeauty.gbt.entity.view.UserFaceView;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.HttpUtil;
import com.kbeauty.gbt.util.ImageUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TrainingService {

	@Autowired
	private StorageService storageSerivce;

	@Autowired
	private UserFaceRepo userFaceRepo;

	@Autowired
	private TrainingRepo trainingRepo;

	@Autowired
	private SkinService skinService;

	@Autowired
	private LessonRepo lessonRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EvaluateRepo evaluateRepo;

	@Autowired
	private ResourceRepo resourceRepo;

	@Autowired
	private LessonMapper mapper;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucket;

	@Value("${spring.cloud.gcp.storage.bucket.ai}")
	private String aiFolder;

	@Value("${spring.cloud.gcp.storage.bucket.training}")
	private String trainingFolder;

	@Value("${beautage.ai.server.url}")
	private String aiServerUrl;

	public void save(Training training) {
		trainingRepo.save(training);
	}

	public void save(Resources resource) {
		resourceRepo.save(resource);
	}

	public Training getTraining(String trainingId) {
		Training training = trainingRepo.findByTrainingId(trainingId);
		return training;
	}

	private RestTemplate getRestTemplate() {
		return HttpUtil.getRestTemplate();
	}

	public TrainingView saveTraining(MultipartFile orgFaceImg, String userId, String fileName, Training training,
			String userFaceName) throws Exception {
		TrainingView view = new TrainingView();
		Lesson lesson = lessonRepo.findByLessonId(training.getLessonId());
		int oldTrainingCheck = mapper.getOldTrainingCheck(training);
		if (oldTrainingCheck > 0) {
			throw new MessageException(ErrMsg.TRAINING_DUPLICATE_DATA);
		}
		CommonDomain basicInfo = new CommonDomain();
		basicInfo.setBasicInfo(CommonUtil.getSysTime(), userId);
		if (lesson == null || training == null) {
			throw new MessageException(ErrMsg.TRAINING_LACK_DATA);
		}
		if (training != null) {
			if (training.isEmpty()) {
				throw new MessageException(ErrMsg.TRAINING_LACK_DATA);
			}
		}

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

//		4. 얼굴정보를 저장한다. ==> 원본용 얼굴 정보임 
//		List<SkinInfo> saveSkinInfo = skinService.saveSkinInfo(visionFace, imgId, basicInfo);

		// 기울기 조정 부분
		visionFace.setWidth(resizeFaceImg.getWidth());
		visionFace.setHeight(resizeFaceImg.getHeight());
		Orientation orientation = visionFace.genOrientation();
		resizeFaceImg = ImageUtil.rotate(resizeFaceImg, orientation, false);
		// 4. 얼굴정보를 저장한다. ==> 정방향 처리된 얼굴 정보임.

		// 5. 얼굴 정보로 파일 클로핑한다.
		// 진단 이미지 cropping
		BufferedImage treatyImg = CroppingUtil.cropNResize(resizeFaceImg, visionFace, CommonConstants.TREATY_SIZE);
		visionFace.adjustDemension(treatyImg.getWidth(), treatyImg.getHeight()); // 진단 이미지에 맞춰서 landmark 조정
		int width = treatyImg.getWidth();
		int height = treatyImg.getHeight();
		ImgCrop imgCrop = new ImgCrop(width, height);
		imgCrop.setChild();

		BufferedImage aiImg = ImageUtil.resize(treatyImg, CommonConstants.AI_SIZE);
		// 잘라진 썸네일 이미지를 storage에 저장한다.
		storagePath = FileUtil.getStoragePath(aiFolder, userId, aiFileName, StoragePath.AI_USER_DAILY);
		String aiUrl = storageSerivce.saveStorage(aiImg, storagePath, orgFaceImg.getContentType());
		if (userFaceName != null && "".equals(userFaceName)) { // 페이스 저장명 들어왔을 시 저장
			UserFace face = new UserFace();
			face.setAge(training.getAge());
			face.setGender(training.getGender());
			face.setSkinTone(training.getSkinTone());
			face.setSeasonColor(training.getSeasonColor());
			face.setFacialContour(training.getFacialContour());
			face.setThumbnail(aiUrl);
			face.setUserId(training.getUserId());
			face.setUserFaceId(CommonUtil.getGuid());
			face.setFaceName(userFaceName);
			face.setBasicInfo(basicInfo);
			face.setStatus(UserFaceStatus.BASIC.getCode());
			face.setWho(UserFaceWho.OTHERS.getCode());
			userFaceRepo.save(face);
		}
		training.setCategory1(lesson.getCategory1());
		training.setCategory2(lesson.getCategory2());
		training.setTrainingId(imgId);
		training.setBeforeImg(url);
		training.setStatus(TrainingStatus.ONE.getCode());
		training.setBasicInfo(basicInfo);
		training.setUserId(userId);
		trainingRepo.save(training);
		view.setTraining(training);

		return view;
	}

	public TrainingView saveTraining2(String userId, String fileName, Training training, String userFaceName)
			throws Exception {
		TrainingView view = new TrainingView();
		Lesson lesson = lessonRepo.findByLessonId(training.getLessonId());
		int oldTrainingCheck = mapper.getOldTrainingCheck(training);
		if (oldTrainingCheck > 0) {
			throw new MessageException(ErrMsg.TRAINING_DUPLICATE_DATA);
		}
		CommonDomain basicInfo = new CommonDomain();
		basicInfo.setBasicInfo(CommonUtil.getSysTime(), userId);
		if (lesson == null || training == null) {
			throw new MessageException(ErrMsg.TRAINING_LACK_DATA);
		}
		if (training != null) {
			if (training.isEmpty()) {
				throw new MessageException(ErrMsg.TRAINING_LACK_DATA);
			}
		}

		String imgId = CommonUtil.getGuid(); // skin id // 랜덤 GUID 생성
		String extention = FileUtil.getExtension(fileName); // 파일 확장자명
		String neoFileName = FileUtil.getNeoFileName(imgId, fileName); // guid.확장자로 파일명 재설정 ex)G837V1227XV92BG72E1.jpg
		String aiFileName = "T_" + neoFileName;

		String storagePath = FileUtil.getStoragePath(trainingFolder, userId, neoFileName, StoragePath.TRAINING);

		if (userFaceName != null && "".equals(userFaceName)) { // 페이스 저장명 들어왔을 시 저장
			UserFace face = new UserFace();
			face.setAge(training.getAge());
			face.setGender(training.getGender());
			face.setSkinTone(training.getSkinTone());
			face.setSeasonColor(training.getSeasonColor());
			face.setFacialContour(training.getFacialContour());
			face.setUserId(training.getUserId());
			face.setUserFaceId(CommonUtil.getGuid());
			face.setFaceName(userFaceName);
			face.setBasicInfo(basicInfo);
			face.setStatus(UserFaceStatus.BASIC.getCode());
			face.setWho(UserFaceWho.OTHERS.getCode());
			userFaceRepo.save(face);
		}
		training.setCategory1(lesson.getCategory1());
		training.setCategory2(lesson.getCategory2());
		training.setTrainingId(imgId);
		training.setStatus(TrainingStatus.ONE.getCode());
		training.setBasicInfo(basicInfo);
		training.setUserId(userId);
		trainingRepo.save(training);
		view.setTraining(training);

		return view;
	}

	public TrainingView updateTraining(MultipartFile faceImg, String userId, String fileName, String trainingId)
			throws Exception {
		Training training = trainingRepo.findByTrainingId(trainingId);
		TrainingView view = new TrainingView();
		if (training != null) {
			if (training.isEmpty()) {
				throw new MessageException(ErrMsg.TRAINING_LACK_DATA);
			}
		}
		String imgId = CommonUtil.getGuid(); // skin id // 랜덤 GUID 생성
		String extention = FileUtil.getExtension(fileName); // 파일 확장자명
		String neoFileName = FileUtil.getNeoFileName(imgId, fileName); // guid.확장자로 파일명 재설정 ex)G837V1227XV92BG72E1.jpg
		String aiFileName = "T_" + neoFileName;

		String storagePath = FileUtil.getStoragePath(trainingFolder, userId, neoFileName, StoragePath.TRAINING);

		// 1. resize img
		BufferedImage bFaceImg = ImageIO.read(faceImg.getInputStream()); // 사진 가져오기
		Orientation orient = ImageUtil.setOrient(faceImg.getInputStream()); // 기울기 체크
		BufferedImage resizeFaceImg = ImageUtil.rotate(bFaceImg, orient, true); // 조정
		String url = storageSerivce.saveStorage(resizeFaceImg, storagePath, faceImg.getContentType());

		// 2. img 구글 전송
		String gsUrl = FileUtil.getGsUrl(bucket, storagePath);
		GoogleFaceBuilder builder = new GoogleFaceBuilder();

		Training returnTraining = trainingRepo.findByTrainingId(training.getTrainingId());
		returnTraining.setAfterImg(url);

		returnTraining.setBasicInfo(userId);
		returnTraining.setStatus(TrainingStatus.TWO.getCode());
		returnTraining.setFinishTime();
		trainingRepo.save(returnTraining);
		view.setTraining(returnTraining);

		return view;

	}

	public TrainingView gradeTraining(String userId, Training training) {
		TrainingView view = new TrainingView();
		Training returnTraining = trainingRepo.findByTrainingId(training.getTrainingId());
		if (returnTraining == null || training.getGrade() == null || "".equals(training.getGrade())
				|| training.getScore() < 0 || training.getComment() == null || "".equals(training.getComment())) {
			throw new MessageException(ErrMsg.TRAINING_LACK_DATA);
		}
		returnTraining.setGrade(training.getGrade());
		returnTraining.setScore(training.getScore());
		returnTraining.setStatus(TrainingStatus.CON.getCode());
		returnTraining.setComment(training.getComment());
		if (training.getEditFacialContour()!=null&&!training.getEditFacialContour().equals("")) {
			returnTraining.setEditFacialContour(training.getEditFacialContour());
		}else {	
			returnTraining.setEditFacialContour(FacialContour.NONE.getCode());
		}
		if (training.getEditSeasonColor()!=null&&!training.getEditSeasonColor().equals("")) {
			returnTraining.setEditSeasonColor(training.getEditSeasonColor());
		}else {
			returnTraining.setEditSeasonColor(SeasonColor.NONE.getCode());
		}
		if (training.getEditSkinTone()!=null&&!training.getEditSkinTone().equals("")) {
			returnTraining.setEditSkinTone(training.getEditSkinTone());
		}else {
			returnTraining.setEditSkinTone(SkinTone.NONE.getCode());
		}
		returnTraining.setBasicInfo(userId);
		returnTraining.setGradeTime(userId);
		trainingRepo.save(returnTraining);

		Evaluate evaluate = new Evaluate(returnTraining, userId);
		evaluate.setBasicInfo(userId);
		evaluateRepo.save(evaluate);

		view.setTraining(returnTraining);

		List<Evaluate> evaluateList = evaluateRepo.findByTrainingId(training.getTrainingId());
		if (evaluateList.size() <= 0)
			evaluateList = new ArrayList<>();
		view.setEvaluateList(evaluateList);

		return view;

	}

	public Training deleteTraining(Training training, String userId) {
		String dateStr = CommonUtil.getSysTime();
		training = getTraining(training.getTrainingId());
		training.setDelete();
		training.setBasicInfo(dateStr, userId);
		save(training);

		return training;
	}

	public List<TrainingView> getTrainingViewList(TrainingCondition condition) {
		List<TrainingView> list = new ArrayList<>();
		List<Training> trainingList = mapper.getTrainingList(condition);
		for (Training training : trainingList) {
			list.add(getTrainingViewByTraining(training));
		}
		return list;
	}

	public TrainingView getTrainingViewByTraining(Training training) {
		return getTrainingViewByTraining(training, false);
	}

	public TrainingView getTrainingViewByTraining(Training training, boolean flag) { // flag 단일조회인지
		TrainingView view = new TrainingView();
		training.setEnumName();
		view.setTraining(training);
		if (flag) {
			String beforeUrl = training.getBeforeImg();
			String afterUrl = training.getAfterImg();
			if (beforeUrl != null && !"".equals(beforeUrl)) {
				FileInfo fileInfo = new FileInfo();
				String array[] = beforeUrl.split("[.]");
				if (array[array.length - 1].length() < 10)
					fileInfo.setExtension(array[array.length - 1]);
				try {
					URL url = new URL(beforeUrl);
					Image image = ImageIO.read(url);
					fileInfo.setWidth(image.getWidth(null));
					fileInfo.setHeight(image.getHeight(null));
					fileInfo.setRatio(((int) ((float) fileInfo.getHeight() / fileInfo.getWidth() * 10)) / 10.0f);
				} catch (Exception e) {

				}
				view.setBeforeImgInfo(fileInfo);
			}
			if (afterUrl != null && !"".equals(afterUrl)) {
				FileInfo fileInfo = new FileInfo();
				String array[] = afterUrl.split("[.]");
				if (array[array.length - 1].length() < 10)
					fileInfo.setExtension(array[array.length - 1]);
				try {
					URL url = new URL(afterUrl);
					Image image = ImageIO.read(url);
					fileInfo.setWidth(image.getWidth(null));
					fileInfo.setHeight(image.getHeight(null));
					fileInfo.setRatio((float) fileInfo.getHeight() / fileInfo.getWidth());
				} catch (Exception e) {

				}
				view.setAfterImgInfo(fileInfo);
			}
			if (training.getStatus().equals(TrainingStatus.CON.getCode())) {
				User evaluateUser = userRepo.findByUserId(training.getEvaluateUserId());
				view.setEvaluateUser(evaluateUser);
			}
			User user = userRepo.findByUserId(training.getUserId());
			view.setUser(user);
			List<Evaluate> evaluateList = evaluateRepo.findByTrainingId(training.getTrainingId());
			if (evaluateList.size() <= 0)
				evaluateList = new ArrayList<>();
			view.setEvaluateList(evaluateList);
		}

		return view;
	}

	public int getTrainingListCnt(TrainingCondition condition) {
		return mapper.getTrainingListCnt(condition);
	}

	public TrainingView getTrainingViewByTrainingId(String trainingId) {
		Training training = trainingRepo.findByTrainingId(trainingId);
		return getTrainingViewByTraining(training, true);
	}

	public Training saveTraining(Training training, String userId) {

		String dateStr = CommonUtil.getSysTime();
		training.setBasicInfo(dateStr, userId);
		save(training);

		return training;
	}

	public User getUserByUserId(String userId) {
		return userRepo.findByUserId(userId);
	}

}