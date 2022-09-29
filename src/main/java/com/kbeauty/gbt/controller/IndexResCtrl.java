package com.kbeauty.gbt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class IndexResCtrl {

	@GetMapping("/api/hello")
	public HashMap hello() {
		HashMap result = new HashMap();
		result.put("message", "안녕하세요");

		return result;
	}
	}
