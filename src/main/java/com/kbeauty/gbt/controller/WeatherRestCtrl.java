package com.kbeauty.gbt.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.service.ContentService;
import com.kbeauty.gbt.service.UserService;
import com.kbeauty.gbt.service.WeatherService;
import com.kbeauty.gbt.util.TokenUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Api(value = "Weather Controller")
@RequestMapping("/v1/weather")
@Slf4j
public class WeatherRestCtrl {
	
	@Autowired
	private WeatherService service;
	
	@RequestMapping(value="/get_product", method=RequestMethod.POST)
	public WeatherView getProductByWeatherCode( HttpServletRequest request,
			@RequestBody WeatherView inputWeatherView) {          
		
		String userId = TokenUtils.getTokenUserId(request); 
		
		WeatherView wtView = service.getWeatherInfoByCode(inputWeatherView,userId);
		
		if(wtView == null) {
			wtView = new WeatherView();
			wtView.setError(ErrMsg.WEATHER_PROC_ERR);
			return wtView;
		}
		
		wtView.setOk();
		
		return wtView;
	}

}
