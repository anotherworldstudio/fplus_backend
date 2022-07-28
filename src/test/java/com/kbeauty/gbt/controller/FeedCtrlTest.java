package com.kbeauty.gbt.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ContentViewType;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.UserStatus;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
@Rollback(true)
public class FeedCtrlTest  extends CommonCtrlTest{
	
	@Autowired
	private MockMvc mock;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	String userId = "bec8b2bc98b54fcdbfcbc8588ce3c26a";
	String contentId = "e568cb72137c4501a8998fabd97e560c";
	
	@Test
	public void saveBasic() throws Exception {
		User user = new User();
		user.setUserName("test02");
		user.setEmail("test02@gmail.com");
		user.setAgreeYn(YesNo.YES.getVal());
		user.setPrivateYn(YesNo.YES.getVal());
		user.setAgeoverYn(YesNo.YES.getVal());
		
		String content = objectMapper.writeValueAsString(user);
		String url = "/v1/feed/save";
		mock.perform(
				 post(url)
				 .headers(getHeader())
				 .content(content)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk())
//		        .andExpect(jsonPath("$.code", is("0000")))
				.andDo(print());
	}
	
	@Test
	public void fileUpload() throws Exception {
		MockMultipartFile firstFile = new MockMultipartFile("file", "filename.txt", "text/plain","some xml".getBytes());		
		mock.perform(
				MockMvcRequestBuilders.multipart("/v1/feed/upload_content")				
				.file(firstFile)
				.headers(getHeader())
				.param("userId", userId)
				.param("contentId", "1ab9162a43cd42c2aac4fb5bc65fd867")
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	@Value("classpath:com/kbeauty/gbt/data/Feed.json")
	Resource resourceFile;
	
	private ContentView getContentViewByFile() throws Exception {
		ContentView view = new ContentView();
		
		File file = resourceFile.getFile();		
		String jsonFile = new String(Files.readAllBytes(file.toPath()));		
		InputStream resource = resourceFile.getInputStream();
		try (BufferedReader reader = new BufferedReader( new InputStreamReader(resource))) {
			String onLine = reader.lines().collect(Collectors.joining("\n"));
		}
		
		ContentView user = objectMapper.readValue(jsonFile, ContentView.class);
		
		return view;
	}
	
	private ContentView getContentViewData() throws Exception {
		ContentView view = new ContentView();
		String contentId = CommonUtil.getGuid();		
		
		Content content = new Content();
		content.setContentId(contentId);
		String inputContent = "좋은 컨테트 입니다.";
		content.setContent(inputContent);
		content.setViewType(ContentViewType.ALL.getCode());
		content.setReplyYn(YesNo.YES.getVal());
		content.setOwnerId(userId);
		
		view.setContent(content);
				
		return view;
	}
	
	
	@Test
	public void create() throws Exception {
//		ContentView view = getContentViewByFile();
		ContentView view = getContentViewData();
				
		
		String inputContent = objectMapper.writeValueAsString(view);
		String url = "/v1/feed/create";
		mock.perform(
				post(url)
				.headers(getHeader())
				.content(inputContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	@Test
	public void createReply() throws Exception {
//		ContentView view = getContentViewByFile();
		ContentView view = getContentViewData();
		String upperContentId = "cd5c3bd387ce4d078c76188befd26886";
		view.getContent().setUpperContentId(upperContentId);
				
		String inputContent = objectMapper.writeValueAsString(view);
		String url = "/v1/feed/create";
		mock.perform(
				post(url)
				.headers(getHeader())
				.content(inputContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	
	@Test
	public void getContentView() throws Exception {
		
	    MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
	    info.add("userId", userId);
	    String url = "/v1/feed/"+userId + "/"+ contentId;

		mock.perform(
				 get(url)
				.headers(getHeader())				
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))				
				.andDo(print());
	}
	
	@Test
	public void saveLike() throws Exception {		
		Likes like = new Likes();
		like.setContentId(contentId);
		like.setOwnerId(userId);
		
		String inputContent = objectMapper.writeValueAsString(like);
		String url = "/v1/feed/save_like";
		mock.perform(
				post(url)
				.headers(getHeader())
				.content(inputContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	@Test
	public void deleteLike() throws Exception {		
		Likes like = new Likes();
		like.setSeq(8);
		like.setContentId(contentId);
		like.setOwnerId(userId);
		like.setLikeId("6b76d58ca4c94243a436ff20c3f6b8c1");
				
		String inputContent = objectMapper.writeValueAsString(like);
		String url = "/v1/feed/delete_like";
		mock.perform(
				post(url)
				.headers(getHeader())
				.content(inputContent)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	@Test
	public void getUserListView() throws Exception {
		ContentCondition condition = new ContentCondition();
		condition.setStatus(UserStatus.REG.getCode());
		
		String content = objectMapper.writeValueAsString(condition);
		
		String url = "/v1/feed/list";
		mock.perform(
				post(url)
				.headers(getHeader())
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}

}
