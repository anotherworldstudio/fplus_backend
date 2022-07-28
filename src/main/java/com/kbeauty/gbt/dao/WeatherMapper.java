package com.kbeauty.gbt.dao;

import java.util.List;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.domain.WeatherProduct;
import com.kbeauty.gbt.entity.view.WeatherCondition;

public interface WeatherMapper {

	public Weather getWeatherProc(String weatherCode);
	public List<Weather> getWeatherList(WeatherCondition condition);
	public List<Weather> getWeatherAndProductList(String weatherCode);
	public List<Weather> getWeatherAndPriorityProductList(String weatherCode);
	public int addProductInWeather(String weathercode, String productSeq);
	public int getWeatherListCnt(WeatherCondition condition);
	public List<Content> getProductListByWeather(Weather weather);
	public List<Content> getPriorityProductList(Weather weather);
	public Weather getWeather(String weatherId);
	
}
