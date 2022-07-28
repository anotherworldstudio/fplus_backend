package com.kbeauty.gbt.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserSkin;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.Sex;
import com.kbeauty.gbt.entity.enums.UserStatus;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.UserCondition;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
@Rollback(true)
public class UserRestCtrlTest extends CommonCtrlTest{
	
	@Autowired
	private MockMvc mock;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	String userId = "bec8b2bc98b54fcdbfcbc8588ce3c26a";
	
	
	
	
	@Test
	public void saveBasic() throws Exception {
		User user = new User();
		user.setUserName("test02");
		user.setEmail("test02@gmail.com");
		user.setAgreeYn(YesNo.YES.getVal());
		user.setPrivateYn(YesNo.YES.getVal());
		user.setAgeoverYn(YesNo.YES.getVal());
		
		String content = objectMapper.writeValueAsString(user);
		String url = "/v1/user/save_basic/"+userId;
		mock.perform(
				 post(url)
				 .headers(getHeader())
				 .content(content)
				 .contentType(MediaType.APPLICATION_JSON)
				 .accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk()).andExpect(jsonPath("$.code", is("0000")))
				.andDo(print());
	}
	
	@Test
	public void savePersonal() throws Exception {
		User user = new User();
		user.setUserId(userId);
		user.setSex(Sex.FEMAIL.getVal());
		user.setCountry("KR");
		user.setBirthDay("20000101");
		
		String content = objectMapper.writeValueAsString(user);
		String url = "/v1/user/save_personal/"+userId;
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
	
	
	@Test
	public void saveSkin() throws Exception {
		UserSkin userSkin = new UserSkin();
		userSkin.setUserId(userId);
		userSkin.setTrouble01    ( YesNo.YES.getVal() );
		userSkin.setTrouble02    ( YesNo.YES.getVal() );
		userSkin.setTrouble03    ( YesNo.YES.getVal() );
		userSkin.setTrouble04    ( YesNo.YES.getVal() );
		userSkin.setTrouble05    ( YesNo.YES.getVal() );
		userSkin.setSkintype01   ( YesNo.YES.getVal() );
		userSkin.setSkintype02   ( YesNo.YES.getVal() );
		userSkin.setSkintype03   ( YesNo.YES.getVal() );
		userSkin.setSkintype04   ( YesNo.YES.getVal() );
		userSkin.setSkintype05   ( YesNo.YES.getVal() );
		userSkin.setSkintype06   ( YesNo.YES.getVal() );
		userSkin.setSkintype07   ( YesNo.YES.getVal() );
		userSkin.setSkintype08   ( YesNo.YES.getVal() );

		
		String content = objectMapper.writeValueAsString(userSkin);
		String url = "/v1/user/save_skin/"+userId;
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

	@Test
	public void saveImg() throws Exception {
		MockMultipartFile firstFile = new MockMultipartFile("faceImg", "filename.txt", "text/plain","some xml".getBytes());		
		
		mock.perform(
				MockMvcRequestBuilders.multipart("/v1/user/save_img")				
				.file(firstFile)
				.headers(getHeader())
				.param("userId", userId)
				.param("fileName", "filename.txt")
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	
	@Test
	public void getUserView() throws Exception {
		
	    MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
	    info.add("userId", userId);
	    String url = "/v1/user/"+userId;

		mock.perform(
				 get(url)
				.headers(getHeader())
				
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))				
				.andDo(print());
	}
	
	@Test
	public void deleteUser() throws Exception {
				
//		String content = objectMapper.writeValueAsString(user);
		String url = "/v1/user/delete/"+userId;
		mock.perform(
				post(url)
				.headers(getHeader())				
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.SUCCESS.getCode())))
		.andDo(print());
	}
	
	@Test
	public void getUserListView() throws Exception {
		UserCondition condition = new UserCondition();
		condition.setStatus(UserStatus.REG.getCode());
		
		String content = objectMapper.writeValueAsString(condition);
		
		String url = "/v1/user/list";
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
	
	@Test
	public void getUserListView2() throws Exception {
		UserCondition condition = new UserCondition();
		condition.setStatus(UserStatus.DEL.getCode());
		
		String content = objectMapper.writeValueAsString(condition);
		
		String url = "/v1/user/list";
		mock.perform(
				post(url)
				.headers(getHeader())
				.content(content)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				)
		.andExpect(status().isOk()).andExpect(jsonPath("$.code", is(ErrMsg.NO_RESULT.getCode())))
		.andDo(print());
	}
	
	
	
	@Test
	public void save() throws Exception {
		User user = new User();
		user.setUserName("test01");
		
		String content = objectMapper.writeValueAsString(user);
		
		log.debug("zzzzzzzzzzz" + content);

		mock.perform(
				 post("/v1/user/save").content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk())				
				.andDo(print());
	}
	
	@Test
	public void saveFollow() throws Exception {		
		Follow follow = new Follow();
		follow.setUserId(userId);
		follow.setFollowerId("75d84aace2514989b6a2825246db7cc5");
//		follow.setFollowYn(YesNo.YES.getVal());
		follow.setFollowYn(YesNo.NO.getVal());
				
		String inputContent = objectMapper.writeValueAsString(follow);
		String url = "/v1/user/follow";
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

}
