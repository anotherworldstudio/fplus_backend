package com.kbeauty.gbt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexResCtrl {

	@GetMapping("/main")
	public String index() {

		return "ㅠㅠㅠㅠㅠㅠㅠㅠㅠ";
		}
	}
