package com.kbeauty.gbt.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.dao.ContentMapper;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.Active;
import com.kbeauty.gbt.entity.enums.ContentStatus;
import com.kbeauty.gbt.entity.enums.ContentType;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.enums.OtherCode;
import com.kbeauty.gbt.entity.enums.OtherStatus;
import com.kbeauty.gbt.entity.enums.OtherType;
import com.kbeauty.gbt.entity.enums.TableToClass;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.MigrationService;
import com.kbeauty.gbt.service.StorageService;
import com.kbeauty.gbt.util.ExcelUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
//@Transactional
//@Rollback(true)
public class MigServiceTest extends CommonCtrlTest{
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private MigrationService service;
	
	@Autowired
	ContentService contentService;
	
	@Autowired
	private ContentMapper contentMapper;
	
	
	@Test
	public void generateOldAiData() throws Exception {
		service.generateOldAiData();
	}
	
	@Test
	public void fileInfo() throws Exception {
		service.insertFileInfo();
	}
	
	@Test
	public void excelTest() throws Exception {
		String ownerId = "ede1212987a8409b9b4fb20c39617d63"; // 
		String loginId = ownerId; 
		MultipartFile file = null;
		User user = null;
		MultipartFile multipartFile = new MockMultipartFile("test.xlsx",
				new FileInputStream(new File("E:\\DEV\\excel\\excel_temp.xlsx")));

		String className = TableToClass.CONTENT.getVal();
		List<Content> list = ExcelUtil.getFirstSheetData(multipartFile.getInputStream(), className);

		className = TableToClass.PRICE.getVal();
		List<Price> priceList = ExcelUtil.getFirstSheetData(multipartFile.getInputStream(), className);

		
		Map<String, Price> map = new HashMap<String, Price>();
		for (Price price : priceList) {
			map.put(price.getContentId(), price);
		}

		String flag = "0";
		Map<String, List<Others>> otherMap = new HashMap<String, List<Others>>();

		Map<String, List<String>> othersDataMap = ExcelUtil.getFirstSheetDataMap(multipartFile.getInputStream(), 8, 15);
		Set<Entry<String, List<String>>> entrySet = othersDataMap.entrySet();
		Others others = null;
		List<String> list2 = null;
		List<Others> otherList = null;
		for (Entry<String, List<String>> entry : entrySet) {
			list2 = entry.getValue();

			OtherCode[] values = OtherCode.values();
			int i = 0;
			otherList = new ArrayList<>();
			for (OtherCode othersCode : values) {
				others = new Others();
				others.setOtherType(OtherType.FEATURE.getCode());
				others.setOtherStatus(OtherStatus.REG.getCode());

				flag = StringUtil.isEmpty(list2.get(i++)) ? "0" : "1";
				others.setOtherOrder(i);
				others.setOtherCode(othersCode.getCode());
				others.setOtherName(othersCode.getVal());
				others.setOtherValue(flag);
				otherList.add(others);
			}
			otherMap.put(entry.getKey(), otherList);
		}

		List<ContentView> listView = new ArrayList<>();
		ContentView view = null;
		String contentId = null;
		long contentSeq;
		for (Content content : list) {
			contentId = content.getContentId();
			contentSeq = contentService.getContentSeq();
			content.setContentId(contentId);
			content.setSeq(contentSeq);
			content.setContentType(ContentType.PRODUCT.getCode());
//			content.setOwnerId(userId);
			content.setReplyYn("Y");	
			content.setViewType(ContentViewType.ALL.getCode());
			content.setActive(Active.PASSIVE.getCode()); 
			content.setStatus(ContentStatus.REG.getCode());
			content.setCategory1(contentMapper.getClass02Code(content.getCategory1()));
			content.setCategory2(contentMapper.getClass03Code(content.getCategory2()));
			content.setOwnerId(ownerId);
			view = new ContentView();
			view.setContent(content);
			view.setPrice(map.get(contentId));
			view.setFeatureList(otherMap.get(contentId));  
			listView.add(view);
		}
		 
		log.debug(contentId);
		for (ContentView contentView : listView) {			
			contentService.create(contentView);
		}

		log.debug(list.toString());
		
	}
	
	@Test
	public void getQuery() throws Exception {
		String start = 	"INSERT INTO `skinpoll` (`seq`, `guid`, `email`, `sex`, `phone`, `country`, `birthyyyy`, `img_dir`, `front_img`, `left_img`, `right_img`, `skin01`, `skin02`, `skin03`, `skin04`, `skin05`, `skin06`, `skin07`, `skin08`, `skin09`, `skin10`, `trouble01`, `trouble02`, `trouble03`, `trouble04`, `trouble05`, `trouble06`, `trouble07`, `trouble08`, `trouble09`, `trouble10`, `trouble11`, `trouble12`, `trouble13`, `trouble14`, `trouble15`, `status`, `ip`, `agent`, `host`, `ageover`, `agree`, `marketing`, `regdate`, `chgdate`, `regtime`, `chgtime`, `reguser`, `chguser`) VALUES ";
		
//		String temp = "(946, 'e57b557924e8418d83dcf22e2e45e72a', '', '', '', '', '', '/home/office/upload/', 'e57b557924e8418d83dcf22e2e45e72a_F.jpg', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'R', '58.229.114.163', 'beautej', 'ACNE', NULL, NULL, '0', '20210201', '20210201', '15:45:01.083', '15:48:17.302', NULL, 'office'),";
		String temp = "(%s, '%s', '', '', '', '', '', '/home/office/upload/', '%s_F.jpg', '', '', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', 'R', '58.229.114.163', 'beautej', 'ACNE', NULL, NULL, '0', '20210201', '20210201', '15:45:01.083', '15:48:17.302', NULL, 'office'),";
		
		String seq = "1000";
		String str = "4beea70f8d2d41c093edc64af68e92bb";
		
		String format = temp.format(temp, seq, str, str);
		log.error(start + format);
	}
	
	
	@Test
	public void saveBasic() throws Exception {				
		String objectName = "ai2/259e2f884b87437381906244e0b7860f/20210206/992643e9f545415bbf59eb43e32ef4e2.jpg";
		String destFilePath = "E:\\tmp\\test4.jpeg";
			
		storageService.downloadObject(objectName, destFilePath);		
	}
	
	public static void main(String[] args) {
		
		String s = "https://storage.googleapis.com/bucket-beautage-com/";
		String str = "https://storage.googleapis.com/bucket-beautage-com/ai/75d84aace2514989b6a2825246db7cc5/20201216/T_fcb2ae942f654cea994d939b481bb078.jpg";
		
		String[] split = str.split("/");
		
		StringBuffer sb = new StringBuffer();
		sb.append("/").append(split[4]);
		sb.append("/").append(split[5]);
		sb.append("/").append(split[6]);
		sb.append("/").append(split[7].substring(2));
		System.out.println(sb.toString());
		
	}
}
