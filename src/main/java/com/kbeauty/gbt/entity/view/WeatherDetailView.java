package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Weather;

import lombok.Data;

@Data
public class WeatherDetailView extends CommonView {
	
	
	private Weather weather;
	private List<Content> productList;
	

}
