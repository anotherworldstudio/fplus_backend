package com.kbeauty.gbt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.dao.SkinRepo;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinItem;
import com.kbeauty.gbt.entity.domain.SkinLevel;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.TableToClass;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.ExcelUtil;
import com.kbeauty.gbt.util.StringUtil;


@Service
public class MigrationService {
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ContentService contentService;
	
	@Autowired
	private SkinService skinService;
	
	@Autowired
	private SkinRepo skinRepo;
	
	
	
	public void saveContent(MultipartFile file, User user) throws Exception {
		// TODO file이 엑셀인지 체크 
		// TODO sheet 명과 테이블이 동일한지 확인 
		
		String className = TableToClass.CONTENT.getVal();
		List<Content> list = ExcelUtil.getFirstSheetData(file.getInputStream(), className);
		String dateStr = CommonUtil.getSysTime();
		String userId = user.getUserId();
		
		for (Content content : list) {	
			if(content.getSeq() == 0) {				
				content.setSeq(contentService.getContentSeq());
			}
			
			if(StringUtil.isEmpty(content.getPath())) {
				content.setPath(StringUtil.getPath(content.getSeq(), "", 0));
			}
			
			content.setBasicInfo(dateStr, userId);
			contentService.save(content);
		}
	}
	
	 
	public void saveSkinLevel(MultipartFile file, User user) throws Exception {
		String className = TableToClass.SKINLEVEL.getVal();
		List<SkinLevel> list = ExcelUtil.getFirstSheetData(file.getInputStream(), className);
		String dateStr = CommonUtil.getSysTime();
		String userId = user.getUserId();
		
		for (SkinLevel skinLevel : list) {
			skinLevel.setBasicInfo(dateStr, userId);
			skinService.save(skinLevel);
		}
	}
	
	public void saveSkinItem(MultipartFile file, User user) throws Exception {
		String className = TableToClass.SKINITEM.getVal();
		List<SkinItem> list = ExcelUtil.getFirstSheetData(file.getInputStream(), className);
		String dateStr = CommonUtil.getSysTime();
		String userId = user.getUserId();
		
		for (SkinItem skinItem : list) {
			skinItem.setBasicInfo(dateStr, userId);
			skinService.save(skinItem);
		}
	}
	
	
	public void generateOldAiData() {
		List<Skin> list = getSkinData();
		List<Skin> neoList = getFile(list);
		getQuery(neoList);		
	}
	
	private List<Skin> getSkinData() {
		List<Skin> list = new ArrayList<Skin>();
		Iterable<Skin> findAll = skinRepo.findAll();
		for (Skin skin : findAll) {
			if( ! skin.isDelete()) {
				list.add(skin);
			}
		}
		return list;
	}
	
	private List<Skin> getFile(List<Skin> list){
		List<Skin> neoList = new ArrayList<Skin>();
//		String objectName = "ai2/259e2f884b87437381906244e0b7860f/20210206/992643e9f545415bbf59eb43e32ef4e2.jpg";
		String destFilePath = "E:\\beautage\\earlybird\\backup\\neo\\";
			
		String objectName = null;
		String fileName = null;
		for (Skin skin : list) {
			objectName = getDownloadUrl(skin.getAiUrl());
			fileName = getId(skin.getAiUrl());
			boolean isOk = storageService.downloadObject(objectName, destFilePath+fileName+"_F.jpg");
			if(isOk) {
				neoList.add(skin);				
			}
		}
		
		return neoList;
	}
	
	private void getQuery(List<Skin> list){		
		String start = 	"INSERT INTO `skinpoll` (`seq`, `guid`, `email`, `sex`, `phone`, `country`, `birthyyyy`, `img_dir`, `front_img`, `left_img`, `right_img`, `skin01`, `skin02`, `skin03`, `skin04`, `skin05`, `skin06`, `skin07`, `skin08`, `skin09`, `skin10`, `trouble01`, `trouble02`, `trouble03`, `trouble04`, `trouble05`, `trouble06`, `trouble07`, `trouble08`, `trouble09`, `trouble10`, `trouble11`, `trouble12`, `trouble13`, `trouble14`, `trouble15`, `status`, `ip`, `agent`, `host`, `ageover`, `agree`, `marketing`, `regdate`, `chgdate`, `regtime`, `chgtime`, `reguser`, `chguser`) VALUES ";
		
//		String temp = "(946, 'e57b557924e8418d83dcf22e2e45e72a', '', '', '', '', '', '/home/office/upload/', 'e57b557924e8418d83dcf22e2e45e72a_F.jpg', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'R', '58.229.114.163', 'beautej', 'ACNE', NULL, NULL, '0', '20210201', '20210201', '15:45:01.083', '15:48:17.302', NULL, 'office'),";
		String temp = "(%s, '%s', '', '', '', '', '', '/home/office/upload/', '%s_F.jpg', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'R', '58.229.114.163', 'beautej', 'ACNE', NULL, NULL, '0', '20210201', '20210201', '15:45:01.083', '15:48:17.302', NULL, 'office'),";
		
		int seq = 1000;
		String str = null;
		String format = null;
		StringBuffer sb = new StringBuffer();
		sb.append(start);
		for (Skin skin : list) {
			str = getId(skin.getAiUrl());
			format = temp.format(temp, seq++, str, str);
			sb.append(format).append("\n");			
		}
		System.out.println("============================================");
		System.out.println(sb.toString());		
	}
	
	private String getId(String url) {
		//  https://storage.googleapis.com/bucket-beautage-com/ai/75d84aace2514989b6a2825246db7cc5/20201216/T_fcb2ae942f654cea994d939b481bb078.jpg
		String[] split = url.split("/");	
		int i = split.length;
		String tImg = split[i-1];
		String fileName = tImg.substring(2);
		
		split = fileName.split("\\.");
		
		return split[0];
	}
	
	public String getDownloadUrl(String url) {
		
//		String s = "https://storage.googleapis.com/bucket-beautage-com/";
//		String str = "https://storage.googleapis.com/bucket-beautage-com/ai/75d84aace2514989b6a2825246db7cc5/20201216/T_fcb2ae942f654cea994d939b481bb078.jpg";
//		
		String[] split = url.split("/");
		
		StringBuffer sb = new StringBuffer();
		sb.append("").append(split[4]);
		sb.append("/").append(split[5]);
		sb.append("/").append(split[6]);
		sb.append("/").append(split[7].substring(2));
		//System.out.println(sb.toString());
		
		return sb.toString();
		
	}


	public void insertFileInfo() {
		contentService.insertFileInfo();
		contentService.insertFileInfo2();
		
	}
	

}
