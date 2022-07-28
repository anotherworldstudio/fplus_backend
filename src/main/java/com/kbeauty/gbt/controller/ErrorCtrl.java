package com.kbeauty.gbt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kbeauty.gbt.entity.enums.ConstantMapKey;
import com.kbeauty.gbt.entity.enums.ErrMsg;

@Controller
@RequestMapping(value = "/error")
public class ErrorCtrl {
	
	@GetMapping(value = "/unauthorized")
	@ResponseBody
    public ResponseEntity<String> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
	
	@GetMapping(value = "/unauthorizedweb")	
	public String unauthorizedWeb() {
		String nextPage = "redirect:/w1/login";
		return nextPage;
	}
	
	@GetMapping(value = "/login_fail")	
	public String go403() {		
		return "error/page_403";
	}
	
	@GetMapping(value = "/validate")	
	public String go400(Model model) {		
		ErrMsg msg = (ErrMsg)model.getAttribute(ConstantMapKey.ERROR_MSG_KEY.toString());
		String errorUrl = (String)model.getAttribute(ConstantMapKey.ERROR_URL.toString());
		
		model.addAttribute("errorMsg", msg.getMsg());
		model.addAttribute("errorCode", msg.getCode());
		model.addAttribute("errorUrl", errorUrl);
		
		
		return "error/page_400";
	}
}
