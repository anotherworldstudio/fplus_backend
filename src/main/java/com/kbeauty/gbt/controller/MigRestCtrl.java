package com.kbeauty.gbt.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.TableToClass;
import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.service.MigrationService;
import com.kbeauty.gbt.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Feed Controller")
@RequestMapping("/v1/mig")
@Slf4j
public class MigRestCtrl {
	
	@Autowired
	MigrationService service;
	@Autowired
	UserService userService;
	
	@ApiOperation(value = "saveConent", notes = "Content 저장 함수")	
	@RequestMapping(value="/save_content", method=RequestMethod.POST, headers="Content-Type=multipart/form-data")  
	public CommonView saveConent(@RequestPart MultipartFile uploadExcel, 
			@RequestParam String userId,
			@RequestParam String tableName,
			HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception{
		CommonView view = new CommonView();
		//사용자 아이디
		User user = userService.getUser(userId);
		if(user == null) {
			view.setError(ErrMsg.NO_USER);
			return view;
		}
		
		try {
			TableToClass tableToClass = TableToClass.valueOf(tableName.toUpperCase());
			switch (tableToClass) {
			case CONTENT:
				saveContent(uploadExcel, user);
				break;
			case SKINITEM:
				saveSkinItem(uploadExcel, user);
				break;
			case SKINLEVEL:
				saveSkinLevel(uploadExcel, user);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			view.setError(ErrMsg.EXCEL_PROCESS_ERR);
			return view;
		}
		
		view.setOk();
		return view;
	}
	
	private void saveContent(MultipartFile file, User user)  throws Exception{
		service.saveContent(file, user);
	}
	
	private void saveSkinLevel(MultipartFile file, User user)  throws Exception{
		service.saveSkinLevel(file, user);
	}
	
	private void saveSkinItem(MultipartFile file, User user)  throws Exception{
		service.saveSkinItem(file, user);
	}

}
