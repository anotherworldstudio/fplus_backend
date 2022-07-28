package com.kbeauty.gbt.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.domain.User;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class LoginRestCtrlTest  extends CommonCtrlTest{
	@Autowired
	private MockMvc mock;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Test
	public void login1() throws Exception {		
		User user = new User();
		user.setUserName("test01");
		user.setEmail("test01@gmail.com");
		
		String content = objectMapper.writeValueAsString(user);
		
		mock.perform(
				 post("/v1/login").content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk())				
				.andDo(print());
	}
	
	@Test
	public void login2() throws Exception {
		
		User user = new User();
		user.setUserName("test01");
		user.setEmail("test01@gmail.com");
		
		String content = objectMapper.writeValueAsString(user);
		
		log.debug("zzzzzzzzzzz" + content);

		mock.perform(
				 post("/v1/login").headers(getHeader()).
				 content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk())				
				.andDo(print());
	}
	
	@Test
	public void duplicate() throws Exception {
	
		User user = new User();
		user.setUserName("string");
		user.setEmail("test01@gmail.com");
		
		String content = objectMapper.writeValueAsString(user);
		
		log.debug("zzzzzzzzzzz" + content);
		

	    MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
	    info.add("userName", "string");
	    info.add("email", "test01@gmail.com");
		

//		mock.perform(get("/v1/duplicate_user").headers(headers)
//				.params(info).content(content).
//				 contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
//				)
//		        .andExpect(status().isOk())				
//				.andDo(print());
	    
		mock.perform(get("/v1/duplicate_user?userName=string").headers(getHeader())
				.
				 contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				)
		        .andExpect(status().isOk())				
				.andDo(print());	    
	}
	
	

}
