package com.kbeauty.gbt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;

@Api(value = "Index Controller")
@Controller
public class IndexCtrl {

	@GetMapping("/w1")
	public String index(Model model) {	
		
		return "admin/main";
	}
	
	
}
