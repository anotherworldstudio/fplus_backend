package com.kbeauty.gbt.service;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;

import javax.imageio.ImageIO;

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
import com.kbeauty.gbt.entity.AiRawScore;
import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.CommonDomain;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.Orientation;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.entity.view.SkinResultView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.HttpUtil;
import com.kbeauty.gbt.util.ImageUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiService {
	
	@Autowired
	StorageService storageSerivce;
	
	@Autowired
	SkinService skinService;

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucket;
	
	@Value("${spring.cloud.gcp.storage.bucket.ai}")
	private String aiFolder;
	
	@Value("${beautage.ai.server.url}")
	private String aiServerUrl;

	
	public static void main(String[] args) {
//		RestTemplate restTemplate = new RestTemplate(factory);
//		String url = "http://testapi.com/search?text=1234"; // 예제니까 애초에 때려박음..
//		Object obj = restTemplate.getForObject("요청할 URI 주소", "응답내용과 자동으로 매핑시킬 java object");
//		System.out.println(obj); 
	}

	private RestTemplate getRestTemplate() {
		return HttpUtil.getRestTemplate();
	}
	
	public SkinResultView deepSkin(MultipartFile orgFaceImg, String userId, String fileName) throws Exception {
		/**
		 * 1. 입력된 파일을 resize
		 * 2. resize 된 파일을 구글에 전송한다.
		 * 3. 얼굴 정보를 리턴한다.
		 * 4. 얼굴정보를 저장한다. (원본 및 분석용 파일) + 얼굴 정보
		 * 5. 얼굴 정보로 파일 클로핑한다.
		 * 6. 얼굴 이미지 / 크로핑 이미지를 ai 서버를 호출한다.
		 * 7. ai score를 가지고 해당 데이터를 조회한다.
		 * 8. 조회한 결과를 리턴한다.   
		 */
		StopWatch stopWatch = new StopWatch();
	    stopWatch.start("이미지 변환");
	    
	    
	    CommonDomain basicInfo = new CommonDomain();
	    basicInfo.setBasicInfo(CommonUtil.getSysTime(), userId);
	    
		String imgId = CommonUtil.getGuid(); // skin id 
		String extention = FileUtil.getExtension(fileName);
		String neoFileName = FileUtil.getNeoFileName(imgId, fileName);
		String aiFileName = "T_" + neoFileName;
		
		String storagePath = FileUtil.getStoragePath(aiFolder, userId, neoFileName, StoragePath.AI_USER_DAILY);
	
		
		//1. resize img
		BufferedImage bFaceImg = ImageIO.read(orgFaceImg.getInputStream());
		
		Orientation orient = ImageUtil.setOrient(orgFaceImg.getInputStream());
		BufferedImage resizeFaceImg = ImageUtil.rotate(bFaceImg, orient, true);
		String url = storageSerivce.saveStorage(resizeFaceImg, storagePath, orgFaceImg.getContentType());
		
		stopWatch.stop();
	    log.info("이미지 변환 수행시간 >> {}", stopWatch.getTotalTimeSeconds());

	    //2. img 구글 전송
		String gsUrl = FileUtil.getGsUrl(bucket, storagePath);
		GoogleFaceBuilder builder = new GoogleFaceBuilder();

		stopWatch.start("구글 vison storage");
		//3. 얼굴 정보리턴한다.
		VisionFace visionFace = builder.getFace(gsUrl);
		if(visionFace == null) {
			throw new MessageException(ErrMsg.AI_FACE_ERR);
		}
		
		//4. 얼굴정보를 저장한다. ==> 원본용 얼굴 정보임 
		List<SkinInfo> saveSkinInfo = skinService.saveSkinInfo(visionFace, imgId, basicInfo);
		
		stopWatch.stop();
		log.info("구글 vison storage 수행시간 >> {}", stopWatch.getTotalTimeSeconds());
		
		stopWatch.start("이미지 분할");
		// 기울기 조정 부분 
		visionFace.setWidth(resizeFaceImg.getWidth()); 
		visionFace.setHeight(resizeFaceImg.getHeight());
		Orientation orientation = visionFace.genOrientation(); 
		resizeFaceImg = ImageUtil.rotate(resizeFaceImg, orientation, false);
		//4. 얼굴정보를 저장한다. ==> 정방향 처리된 얼굴 정보임.
		
		//5. 얼굴 정보로 파일 클로핑한다.
	    //진단 이미지 cropping 
//	    BufferedImage treatyImg = CroppingUtil.crop(resizeFaceImg, visionFace);
	    BufferedImage treatyImg = CroppingUtil.cropNResize(resizeFaceImg, visionFace, CommonConstants.TREATY_SIZE);
	    visionFace.adjustDemension(treatyImg.getWidth(), treatyImg.getHeight()); //진단 이미지에 맞춰서 landmark 조정 
		int width =  treatyImg.getWidth();
		int height = treatyImg.getHeight();		
		ImgCrop imgCrop = new ImgCrop(width, height);		
		imgCrop.setChild();
		
		BufferedImage tZoneTopImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.TZONE_TOP);
		BufferedImage tZoneBtmImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.TZONE_BOTTOM);
		BufferedImage uZoneLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.UZONE_LEFT);
		BufferedImage uZoneRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.UZONE_RIGHT);
		
		BufferedImage forheadLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.FORHEAD_LEFT);
		BufferedImage forheadMidImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.FORHEAD_MID);
		BufferedImage forheadRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.FORHEAD_RIGHT);
		
		
		BufferedImage glabellaImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.GLABELLA);
		BufferedImage eyeLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.EYE_LEFT);
		BufferedImage eyeRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.EYE_RIGHT);
		
		BufferedImage cheekLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.CHEEK_LEFT);
		BufferedImage cheekRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.CHEEK_RIGHT);
		
		BufferedImage mouthImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.MOUTH);
		
		BufferedImage mouthLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.MOUTH_LEFT);
		BufferedImage mouthRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.MOUTH_RIGHT);
		
		
		BufferedImage aiImg = ImageUtil.resize(treatyImg, CommonConstants.AI_SIZE);
		
		// aiImg 분석용 이미지를 storage에 저장한다. 
		storagePath = FileUtil.getStoragePath(aiFolder, userId, aiFileName, StoragePath.AI_USER_DAILY);
		String aiUrl = storageSerivce.saveStorage(aiImg, storagePath, orgFaceImg.getContentType());
		
		// TODO 여기 수정
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.TZONE_TOP), StoragePath.AI_USER_DAILY);
		String TTUrl = storageSerivce.saveStorage(tZoneTopImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.TZONE_BOTTOM), StoragePath.AI_USER_DAILY);
		String TBUrl = storageSerivce.saveStorage(tZoneBtmImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.UZONE_LEFT), StoragePath.AI_USER_DAILY);
		String ULUrl = storageSerivce.saveStorage(uZoneLeftImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.UZONE_RIGHT), StoragePath.AI_USER_DAILY);
		String URUrl = storageSerivce.saveStorage(uZoneRightImg, storagePath, orgFaceImg.getContentType());
		
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.FORHEAD_LEFT), StoragePath.AI_USER_DAILY);
		String FLUrl = 	storageSerivce.saveStorage(forheadLeftImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.FORHEAD_MID), StoragePath.AI_USER_DAILY);
		String FMUrl = storageSerivce.saveStorage(forheadMidImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.FORHEAD_RIGHT), StoragePath.AI_USER_DAILY);
		String FRUrl = storageSerivce.saveStorage(forheadRightImg, storagePath, orgFaceImg.getContentType());
		
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.GLABELLA), StoragePath.AI_USER_DAILY);
	 	String GLUrl = storageSerivce.saveStorage(glabellaImg, storagePath, orgFaceImg.getContentType());
		
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.EYE_LEFT), StoragePath.AI_USER_DAILY);
		String ELUrl = storageSerivce.saveStorage(eyeLeftImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.EYE_RIGHT), StoragePath.AI_USER_DAILY);
		String ERUrl = storageSerivce.saveStorage(eyeRightImg, storagePath, orgFaceImg.getContentType());
		
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.CHEEK_LEFT), StoragePath.AI_USER_DAILY);
		String CLUrl = storageSerivce.saveStorage(cheekLeftImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.CHEEK_RIGHT), StoragePath.AI_USER_DAILY);
		String CRUrl = 	storageSerivce.saveStorage(cheekRightImg, storagePath, orgFaceImg.getContentType());
		
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.MOUTH), StoragePath.AI_USER_DAILY);
		String MOUrl = storageSerivce.saveStorage(mouthImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.MOUTH_LEFT), StoragePath.AI_USER_DAILY);
		String MLUrl = storageSerivce.saveStorage(mouthLeftImg, storagePath, orgFaceImg.getContentType());
		storagePath = FileUtil.getStoragePath(aiFolder, userId, CroppingUtil.getCropFileName(neoFileName, FaceEnum.MOUTH_RIGHT), StoragePath.AI_USER_DAILY);
		String MRUrl = storageSerivce.saveStorage(mouthRightImg, storagePath, orgFaceImg.getContentType());
		                
		stopWatch.stop();
		log.info("이미지 분할 수행시간 >> {}", stopWatch.getTotalTimeSeconds());
		
		stopWatch.start("AI 분석");
		//6. 얼굴 이미지 / 크로핑 이미지를 ai 서버를 호출한다.
		AiRawScore aiScore = getAiScore(aiImg, tZoneTopImg, tZoneBtmImg, uZoneLeftImg, uZoneRightImg, fileName);
		log.info("AI 분석 >> {}", stopWatch.getTotalTimeSeconds());
		
		if(aiScore.getGlasses() == 1 ) {
			throw new MessageException(ErrMsg.AI_GLASSES_ERR);
		}
		
		if(aiScore.getMask() == 1 ) {
			throw new MessageException(ErrMsg.AI_MASK_ERR);
		}
		
		//7. ai score를 가지고 해당 데이터를 조회한다.
		SkinResultView view = getSkinResultView(aiScore, userId);		
		view.setImgUrl(aiUrl);
		view.setTTUrl(TTUrl);
		view.setTBUrl(TBUrl);
		view.setULUrl(ULUrl);
		view.setURUrl(URUrl);
		view.setFLUrl(FLUrl);
		view.setFMUrl(FMUrl);
		view.setFRUrl(FRUrl);
		view.setGLUrl(GLUrl);
		view.setELUrl(ELUrl);
		view.setERUrl(ERUrl);
		view.setCRUrl(CRUrl);
		view.setCLUrl(CLUrl);
		view.setMLUrl(MLUrl);
		view.setMOUrl(MOUrl);
		view.setMRUrl(MRUrl);
		
		view.setImgId(imgId);		
		view.setUserId(userId);
		view.setProcessDate(basicInfo.getRegDate());
		view.setSkinInfoList(saveSkinInfo); 
		
		String shareLink = skinService.getShareLink(view.getImgId()); // imgId가 skinId가 된다. 
		view.setShareLink(shareLink);
				
		skinService.saveSkinResultView(view, basicInfo);
		 
		//8. 조회한 결과를 리턴한다.
		return view;
	}

	public SkinResultView getSkinResultView(AiRawScore aiScore, String userId) {
		SkinResultView skinResultView = skinService.getSkinResultView(aiScore, userId);
		return skinResultView;
	}	

	private AiRawScore getAiScore(BufferedImage aiImg, 
			BufferedImage tZoneTopImg,
			BufferedImage tZoneBtmImg,
			BufferedImage uZoneLeftImg,
			BufferedImage uZoneRightImg, String fileName) throws Exception{
		RestTemplate restTemplate = getRestTemplate();
		
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		ByteArrayResource aiImgResource = FileUtil.getByteArrayResource(aiImg, fileName);
		map.add("faceImg", aiImgResource);		
		
		aiImgResource = FileUtil.getByteArrayResource(tZoneTopImg, fileName);
		map.add("tZoneTopImg", aiImgResource);
		
		aiImgResource = FileUtil.getByteArrayResource(tZoneBtmImg, fileName);
		map.add("tZoneBtmImg", aiImgResource);
		
		aiImgResource = FileUtil.getByteArrayResource(uZoneLeftImg, fileName);
		map.add("uZoneLeftImg", aiImgResource);
		
		aiImgResource = FileUtil.getByteArrayResource(uZoneRightImg, fileName);
		map.add("uZoneRightImg", aiImgResource);
		
//		String url = "http://ai.beautej.com:8080/ai_predict";
//		String url = "http://localhost:5000/ai_predict";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(aiServerUrl);		
		URI uri = builder.build().encode().toUri();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		AiRawScore aiScore = new AiRawScore();
		try {
			ResponseEntity<AiRawScore> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, AiRawScore.class);
			aiScore = responseEntity.getBody();
			log.info("===============*****************************************===============");
			log.info(aiScore.toString());
			log.info("=====================================================================");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("AI 서버 조회 중 오류", e);
			throw new MessageException(ErrMsg.AI_PROCESS_ERR);
		}
		
		return aiScore;
	}



}