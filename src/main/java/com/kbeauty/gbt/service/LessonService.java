package com.kbeauty.gbt.service;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.dao.CountRepo;
import com.kbeauty.gbt.dao.FileInfoRepo;
import com.kbeauty.gbt.dao.LessonFaceRepo;
import com.kbeauty.gbt.dao.LessonMapper;
import com.kbeauty.gbt.dao.LessonRepo;
import com.kbeauty.gbt.dao.ResourceRepo;
import com.kbeauty.gbt.dao.TrainingRepo;
import com.kbeauty.gbt.dao.UserFaceRepo;
import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.FileInfo;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.LessonFace;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.domain.WeatherProduct;
import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.CodeVal;
import com.kbeauty.gbt.entity.enums.ContentActive;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.FileInfoType;
import com.kbeauty.gbt.entity.enums.LessonCategory1;
import com.kbeauty.gbt.entity.enums.LessonFaceType;
import com.kbeauty.gbt.entity.enums.LessonType;
import com.kbeauty.gbt.entity.enums.LikeType;
import com.kbeauty.gbt.entity.enums.ResourceType;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.entity.enums.UserFaceWho;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.LessonCondition;
import com.kbeauty.gbt.entity.view.LessonView;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.FileUtil;
import com.kbeauty.gbt.util.StringUtil;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LessonService {
	
	public final static String NO_CONTENT_IMG_PATH = "/images/no_user_img.png";
	
	@Autowired
	private LessonMapper lessonMapper;

	@Autowired
	private LessonRepo lessonRepo;
	
	@Autowired
	private LessonFaceRepo lessonFaceRepo;

	@Autowired
	private ResourceRepo resourceRepo;
	
	@Autowired
	private TrainingRepo trainingRepo;

	@Autowired
	private FileInfoRepo fileInfoRepo;

	@Autowired
	private CountRepo countRepo;
	
	@Autowired
	private UserFaceRepo userFaceRepo;

	@Autowired
	private StorageService uploadSerivce;
	
	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucket;

	@Value("${spring.cloud.gcp.storage.bucket.content}")
	private String contentFolder;

	@Value("${spring.cloud.gcp.storage.url}")
	private String storageUrl;
	
	public void save(Lesson lesson) {
		lessonRepo.save(lesson);
	}
	public void save(Resources resource) {
		resourceRepo.save(resource);
	}

	private String getUrl(Resources resource) {
		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(resource.getDir()).append("/").append(resource.getFilename());
		return sb.toString();

	}
	public String getUrl(Lesson lesson) {
		if (StringUtil.isEmpty(lesson.getMainDir())) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(storageUrl).append("/").append(lesson.getMainDir()).append("/").append(lesson.getMainFileName());
		return sb.toString();

	}
	
	public Lesson getLesson(String lessonId) {
		Lesson lesson = lessonRepo.findByLessonId(lessonId);
		return lesson;
	}
	
	public Lesson deleteLesson(Lesson lesson, String userId) {
		String dateStr = CommonUtil.getSysTime();
		lesson = getLesson(lesson.getLessonId());
		lesson.setDelete();
		lesson.setBasicInfo(dateStr, userId); 	
		save(lesson);
		
		List<Resources> resourceList = resourceRepo.findByContentId(lesson.getLessonId());
		if(resourceList.size()>0) {
			for (Resources resources : resourceList) {
				resources.setDelete();
				save(resources);
			}
		}
		return lesson;
	}
	
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
	
	public List<LessonView> getLessonViewList(LessonCondition condition) {
			List<LessonView> list = new ArrayList<>();
			List<Lesson> lessonList = new ArrayList<>();
			Map<String, LessonView> lessonMap = new HashMap<String, LessonView>();
			if(condition.isUserFace()) {
//					condition.setLessonFaceType(LessonFaceType.AGE.getCode());
//					condition.setLessonFaceCategory(condition.getAge());
//					lessonList = lessonMapper.getLessonListByUserFace(condition);
//						for (Lesson lesson : lessonList) {
//							LessonView view1 = getLessonViewByLesson(lesson);
//								view1.setAge(true);
//								view1.setOrders(view1.getOrders()+1);
//								lessonMap.put(lesson.getLessonId(), view1);
//								list.add(view1);
//						}
//						
				condition.setLessonFaceType(LessonFaceType.FACIALCONTOUR.getCode());
				condition.setLessonFaceCategory(condition.getFacialContour());
				lessonList = lessonMapper.getLessonListByUserFace(condition);
					for (Lesson lesson : lessonList) {
						LessonView view1 = getLessonViewByLesson(lesson);
							view1.setFacialContour(true);
							view1.setOrders(view1.getOrders()+1);
							lessonMap.put(lesson.getLessonId(), view1);
							list.add(view1);
					}
					
					condition.setLessonFaceType(LessonFaceType.SEASONCOLOR.getCode());
					condition.setLessonFaceCategory(condition.getSeasonColor());
					lessonList = lessonMapper.getLessonListByUserFace(condition);
					for (Lesson lesson : lessonList) {
						LessonView view = lessonMap.get(lesson.getLessonId());
						if(view == null) {
							view = getLessonViewByLesson(lesson);
							view.setOrders(view.getOrders()+1);
							view.setSeasonColor(true);
							lessonMap.put(lesson.getLessonId(), view);
							list.add(view);
						}else {
							list.remove(view);
							view.setOrders(view.getOrders()+1);
							view.setSeasonColor(true);
							lessonMap.put(lesson.getLessonId(), view);
							list.add(view);
						}
					}
					condition.setLessonFaceType(LessonFaceType.SKINTONE.getCode());
					condition.setLessonFaceCategory(condition.getSkinTone());
					lessonList = lessonMapper.getLessonListByUserFace(condition);
					for (Lesson lesson : lessonList) {
						LessonView view = lessonMap.get(lesson.getLessonId());
						if(view == null) {
							view = getLessonViewByLesson(lesson);
							view.setOrders(view.getOrders()+1);
							view.setSkinTone(true);
							lessonMap.put(lesson.getLessonId(), view);
							list.add(view);
						}else {
							list.remove(view);
							view.setOrders(view.getOrders()+1);
							view.setSkinTone(true);
							lessonMap.put(lesson.getLessonId(), view);
							list.add(view);
						}
					}
//					condition.setLessonFaceType(LessonFaceType.GENDER.getCode());
//					condition.setLessonFaceCategory(condition.getGender());
//					lessonList = lessonMapper.getLessonListByUserFace(condition);
//					for (Lesson lesson : lessonList) {
//						LessonView view = lessonMap.get(lesson.getLessonId());
//						if(view == null) {
//							view = getLessonViewByLesson(lesson);
//							view.setOrders(view.getOrders()+1);
//							view.setGender(true);
//							lessonMap.put(lesson.getLessonId(), view);
//							list.add(view);
//						}else {
//							list.remove(view);
//							view.setOrders(view.getOrders()+1);
//							view.setGender(true);
//							lessonMap.put(lesson.getLessonId(), view);
//							list.add(view);
//						}
//					}
					Collections.sort(list,Collections.reverseOrder());
					
					int page = condition.getPage(); 
					int perPage = condition.getPerPageNum();
					if(perPage <= 0 || perPage > 100) {
						perPage = 10;
					}
					page = (page-1) * perPage;
					List<LessonView> returnList = new ArrayList<LessonView>();
					for (int i = 0; i < perPage; i++) {
						if(page<list.size()) {
						returnList.add(list.get(page++));
						}
					}
					list = returnList ;
			}else { 
				lessonList = lessonMapper.getLessonList(condition);
				for (Lesson lesson : lessonList) {
					list.add(getLessonViewByLesson(lesson));
				}	
			}
			return list;
	}
	private LessonView getLessonViewByLesson(Lesson lesson) {
		return getLessonViewByLesson(lesson,null);
	}
	private LessonView getLessonViewByLesson(Lesson lesson, String userId) {
		LessonView view = new LessonView();
		
		String lessonId = lesson.getLessonId();
		if(userId !=null && !"".equals(userId)) {
			Training training = new Training();
			training.setLessonId(lessonId);
			training.setUserId(userId);		
			training = lessonMapper.getTrainingByLesson(training);
			view.setTraining(training);
		}
		List<Resources> resourceList = new ArrayList<>();
		List<Resources> tempResourceList = resourceRepo.findByContentId(lessonId,
				Sort.by(Sort.Direction.ASC, "orders"));

		for (Resources resources : tempResourceList) {
			if (resources.isDelete()) {
					continue;
			}
			if (ResourceType.isStorageUrl(resources.getResourceType())) {
				resources.setResourceUrl(getUrl(resources));
				if(resources.getFilename()!=null&&!"".equals(resources.getFilename())&&resources.getResourceUrl()!=null&&"".equals(resources.getResourceUrl())) {
					resources.setFileInfo(getFileInfo(FileInfoType.RESOUCES.getCode(),resources.getFilename(),resources.getResourceUrl()));
				}
				resources.setLink(resources.getUrl());
				
			} else if (ResourceType.isUTubeUrl(resources.getResourceType())) {
				resources.setResourceUrl("https://www.youtube.com/embed/" + resources.getResourceName()
						+ "?theme=dark&autoplay=1&autohide=0&cc_load_policy=1&mute=1&loop=1&controls=0&disablekb=1&rel=0&iv_load_policy=3&fs=0&playlist="
						+ resources.getResourceName() + "&modestbranding=1&showinfo=0");
				resources.setLink(resources.getUrl());
			} 
			resources.setEnumName();
			resourceList.add(resources);
		}
		
		if (lesson.isMainImg()) {
			lesson.setMainUrl(getUrl(lesson));
//			lesson.setMainImgData(getImageData(getUrl(content)));//ImageData 주석
//			content.setMainImgData(getImageData(content.getMainUrl()));
		} else {
			lesson.setMainUrl(NO_CONTENT_IMG_PATH);
		}
		
		List<LessonFace> faceList;
		LessonFace face = new LessonFace();
		face.setLessonId(lessonId);
		
		faceList = lessonMapper.getLessonFaceListByLessonId(face);
			for (LessonFace lessonFace : faceList) {
				lessonFace.setEnumName();
			}
		view.setLessonFaceList(faceList);
			
//		face.setType(LessonFaceType.AGE.getCode());
//		faceList = lessonMapper.getLessonFaceListByLessonIdAndType(face);
//			for (LessonFace lessonFace : faceList) {
//				lessonFace.setEnumName();
//			}
//		view.setAgeList(faceList);
//		
//		face.setType(LessonFaceType.FACIALCONTOUR.getCode());
//		faceList = lessonMapper.getLessonFaceListByLessonIdAndType(face);
//		for (LessonFace lessonFace : faceList) {
//			lessonFace.setEnumName();
//		}
//		view.setFacialContourList(faceList);
//		
//		face.setType(LessonFaceType.SEASONCOLOR.getCode());
//		faceList = lessonMapper.getLessonFaceListByLessonIdAndType(face);
//		for (LessonFace lessonFace : faceList) {
//			lessonFace.setEnumName();
//		}
//		view.setSeasonColorList(faceList);
//		
//		face.setType(LessonFaceType.SKINTONE.getCode());
//		faceList = lessonMapper.getLessonFaceListByLessonIdAndType(face);
//		for (LessonFace lessonFace : faceList) {
//			lessonFace.setEnumName();
//		}
//		view.setSkinToneList(faceList);
//		
//		face.setType(LessonFaceType.GENDER.getCode());
//		faceList = lessonMapper.getLessonFaceListByLessonIdAndType(face);
//		for (LessonFace lessonFace : faceList) {
//			lessonFace.setEnumName();
//		}
//		view.setGenderList(faceList);
//		
//		face.setType(LessonFaceType.TYPE.getCode());
//		faceList = lessonMapper.getLessonFaceListByLessonIdAndType(face);
//		for (LessonFace lessonFace : faceList) {
//			lessonFace.setEnumName();
//		}
//		view.setTypeList(faceList);
		
//		Count count = countRepo.findByContentId(lessonId);
//		if (count == null) {
//			count = new Count();
		
//		}
//		view.setCount(count);
		
		lesson.setEnumName();
		view.setSeq(lesson.getSeq());
		view.setLesson(lesson);
		view.setResourceList(resourceList);

		return view;
	}
	
	public int getLessonListCnt(LessonCondition condition) {
		if(condition.isUserFace()) {
			return lessonMapper.getLessonListCntByUserFace(condition);
		}else {
			return lessonMapper.getLessonListCnt(condition);
		}
		
	}
	
	public LessonCondition checkUserFace(LessonCondition condition) {
		if(condition.isUserFace()) {
			UserFace myFace = userFaceRepo.findByUserIdAndWho(condition.getSearchUserid(), UserFaceWho.MY.getCode());
			if(myFace == null) {
				throw new MessageException(ErrMsg.USER_FACE_MY_ERR);
			}
			condition.setAge(myFace.getAge());
			condition.setSeasonColor(myFace.getSeasonColor());
			condition.setFacialContour(myFace.getFacialContour());
			condition.setSkinTone(myFace.getSkinTone());
		}
		return condition;
	}
	public String getLessonId() {
		return CommonUtil.getGuid();
	}
	public long getLessonSeq() {
		long seq = lessonMapper.getLessonSeq();
		return seq;
	}
	public LessonView create(LessonView view) {
		String dateStr = CommonUtil.getSysTime();
		Lesson lesson = view.getLesson();
		String lessonId = lesson.getLessonId();
		if (StringUtil.isEmpty(lessonId)) {
			lessonId = CommonUtil.getGuid();
			lesson.setLessonId(lessonId);
		}
		
//		long seq = getLessonSeq();
		String userId = lesson.getOwnerId();
//		lesson.setSeq(seq);

		if (StringUtil.isEmpty(lesson.getActive())) {
			lesson.setActive(ContentActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(lesson.getStatus())) {
			lesson.setStatus(ContentStatus.REG.getCode());
		}

		lesson.setBasicInfo(dateStr, userId);
		if(lesson.getCategory1().equals(LessonCategory1.ALL.getCode())) {
			lesson.setCategory2(null);
		}
		save(lesson);

		return view;
	}
	public List<Resources> createFiles(List<MultipartFile> files, String userId, String contentId, List<String> title,
			List<String> content, List<String> url) throws IOException {
		return createFiles(files, userId, contentId, title, content, url,null);
	}
	public List<Resources> createFiles(List<MultipartFile> files, String userId, String contentId, List<String> title,
			List<String> content, List<String> url, List<String> category) throws IOException {
		List<Resources> list = new ArrayList<>();
		Resources resource = null;
		int order = 0;
		int i = 0;
		for (MultipartFile file : files) {
			resource = getResources(file, userId, contentId, title.get(i), content.get(i), url.get(i),null,category.get(i));
			resource.setOrders(++order);
			list.add(resource);
			i++;
		}

		list = createFile(files, userId, list);

		return list;
	}
	public Resources getResources(MultipartFile file, String userId, String contentId, String title, String content,
			String url) {
		return getResources(file, userId, contentId, title, content, url, null);
	}

	public Resources getResources(MultipartFile file, String userId, String contentId, String title, String content,
			String url, String resourceType) {
		return getResources(file, userId, contentId, title, content, url, resourceType,null);
	}

		
	public Resources getResources(MultipartFile file, String userId, String contentId, String title, String content,
			String url, String resourceType,String category) {
		Resources resource = new Resources();
		resource.setContentId(contentId);
		String resourceId = CommonUtil.getGuid();
		resource.setResourceId(resourceId);
		resource.setResourceTitle(title);
		resource.setResourceContent(content);
		resource.setResourceCategory(category);

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
	
	public Lesson createLessonFile(MultipartFile file, String userId, Lesson lesson) throws IOException {
		String mainFilename = saveImgFile(file, userId);

		String dir = bucket;
		lesson.setMainDir(dir);
		lesson.setMainFileName(mainFilename);

		uploadSerivce.saveUserImg(file, mainFilename);

		return lesson;
	}
	
	private String saveImgFile(MultipartFile file, String userId) {
		String fileName = file.getOriginalFilename();

		String imgId = getLessonId(); // 신규로 발생하게함.
		String neoFileName = FileUtil.getNeoFileName(imgId, fileName); // 확장자는 여기서 붙
		String mainFilename = FileUtil.getStoragePath(contentFolder, userId, neoFileName, StoragePath.CONTENT_USER);
		return mainFilename;
	}
	public LessonView getLessonView(String lessonId) {
		return getLessonView(lessonId,null);
	}
	public LessonView getLessonView(String lessonId, String userId) {
		Lesson lesson = lessonRepo.findByLessonId(lessonId);
		return getLessonViewByLesson(lesson,userId);
	}

	public Lesson saveLesson(Lesson lesson, String userId) {
		String dateStr = CommonUtil.getSysTime();


		if (StringUtil.isEmpty(lesson.getActive())) {
			lesson.setActive(ContentActive.PASSIVE.getCode());
		}

		if (StringUtil.isEmpty(lesson.getStatus())) {
			lesson.setStatus(ContentStatus.REG.getCode());
		}

		lesson.setBasicInfo(dateStr, userId);
		save(lesson);

		return lesson;
	}

	public  <E extends Enum<E> & CodeVal> List<LessonFace> getEnumCodeAndVal(E[] values) {
		List<LessonFace> faceList = new ArrayList<>();
	        for (E e : values) {
	        	LessonFace face = new LessonFace();
	        	String code = e.getCode();
	        	face.setCategory(code);
	        	String Val = e.getVal();
	        	face.setCategoryName(Val);
	        	faceList.add(face);
	        }
        return faceList;
    }
	public LessonFace addLessonFace(LessonFace lessonFace, String userId) {
		lessonFace.setLessonFaceId(getLessonId());
		lessonFace.setBasicInfo(userId);
		lessonFaceRepo.save(lessonFace);
		return lessonFace;
	}
	public List<LessonFace> getLessonFaceOverlapCheck(LessonFace lessonFace) {
		return lessonMapper.getLessonFaceOverlapCheck(lessonFace);
	}
	public List<LessonFace> deleteLessonFace(String lessonId, String lessonFaceId, String userId) {
		LessonFace face = lessonFaceRepo.findByLessonFaceId(lessonFaceId);
		face.setDelete();
		face.setBasicInfo(userId);
		lessonFaceRepo.save(face);
		
		List<LessonFace> faceList = lessonMapper.getLessonFaceListByLessonId(face);
		
		return faceList;
	}
	
	public void copyLesson(String copyLessonId,String userId) {
		Lesson lesson = lessonRepo.findByLessonId(copyLessonId);
		List<LessonFace> faceList = lessonFaceRepo.findByLessonId(copyLessonId);
		List<Resources> resourceList = resourceRepo.findByContentId(copyLessonId);
		
		String newLessonId = CommonUtil.getGuid();
		lesson.setSeq(0);
		lesson.setLessonId(newLessonId);
		lesson.setActive(Active.PASSIVE.getCode());	
		lesson.setOwnerId(userId);
		lesson.setBasicInfo(userId);
		lessonRepo.save(lesson);
		
		if(faceList.size()>0) {
			for (LessonFace face: faceList) {
				if(face.isDelete()) {
					continue;
				}
				face.setSeq(0);
				face.setLessonFaceId(CommonUtil.getGuid());
				face.setLessonId(newLessonId);
				face.setBasicInfo(userId);
				lessonFaceRepo.save(face);
			}
		}
		if(resourceList.size()>0) {
			for (Resources resources : resourceList) {
				if( resources.isDelete()) {
					continue;
				}
				resources.setSeq(0);
				resources.setResourceId(CommonUtil.getGuid());
				resources.setContentId(newLessonId);
				resources.setBasicInfo(userId);
				resourceRepo.save(resources);
			}
		}
	}
}