package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Weather;

import lombok.Data;

@Data
public class WeatherView extends CommonView {
	
	private String weatherMSG = "";
	private List<Weather> weatherList;
	private List<Content> productList;

}
