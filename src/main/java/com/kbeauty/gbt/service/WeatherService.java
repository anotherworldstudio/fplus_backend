package com.kbeauty.gbt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbeauty.gbt.dao.WeatherMapper;
import com.kbeauty.gbt.dao.WeatherProductRepo;
import com.kbeauty.gbt.dao.WeatherRepo;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.domain.WeatherProduct;
import com.kbeauty.gbt.entity.enums.OtherStatus;
import com.kbeauty.gbt.entity.enums.OtherType;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.ContentCondition;
import com.kbeauty.gbt.entity.view.ContentView;
import com.kbeauty.gbt.entity.view.UserListView;
import com.kbeauty.gbt.entity.view.WeatherCondition;
import com.kbeauty.gbt.entity.view.WeatherView;
import com.kbeauty.gbt.util.CommonUtil;

@Service
public class WeatherService {

	@Autowired
	private WeatherRepo repo;
	
	@Autowired
	private WeatherProductRepo weatherProductRepo;

	@Autowired
	private WeatherMapper weatherMapper;
	
	@Autowired
	private ContentService contentService;

	public WeatherView getWeatherInfoByCode(WeatherView inputWeatherView, String userId) {
		WeatherView weatherview = new WeatherView(); // return 할 weatherView
		List<Weather> weatherList = inputWeatherView.getWeatherList(); // 전달받은 weatherView 의 weatherList (날씨코드)
		List<Weather> outWeatherList = new ArrayList<>(); // return 할 weatherView 안에 넣을 weatherList (상품코드 넣을곳)
		Weather outWeather = null;
		List<Weather> wtList = null; // 데이터베이스에서 잠시 뽑아오는 리스트 랜덤으로 Weather 하나만 배출
		for (Weather wt : weatherList) {
			wtList = null;
			wtList = weatherMapper.getWeatherAndPriorityProductList(wt.getWeatherCode());
			if (wtList.size() > 0) {
				outWeather = wtList.get(getRandom(wtList.size()));
			} else {
				wtList = weatherMapper.getWeatherAndProductList(wt.getWeatherCode());
				if (wtList.size() > 0) {
					outWeather = wtList.get(getRandom(wtList.size()));
				}
			}
			outWeather.setContentView(contentService.getContentView(outWeather.getRefId(), userId));
			if(!outWeatherList.contains(outWeather)) {
				outWeatherList.add(outWeather);
			}
		}
		weatherview.setWeatherList(outWeatherList);

		return weatherview;
	}

	public List<Weather> getWeatherList(WeatherCondition condition) {
		List<Weather> list = new ArrayList<>();
		String pdName = condition.getProductName();
		if(!"".equals(pdName)&&pdName!=null) {
			return getWeatherListByProductName(pdName);
		}
		List<Weather> weatherList = weatherMapper.getWeatherList(condition);
		
		for (Weather weather : weatherList) {
			weather.setTypeName();
		}
		if (weatherList != null)
			list = weatherList;

		return list;
	}
	public List<Weather> getWeatherListByProductName(String productName){
		List<Weather> wtList = new ArrayList<>();
		Weather wt;
		List<Content> pdList = contentService.searchProductName(productName);
		for (Content content : pdList) {
			List<WeatherProduct> weatherProductList = weatherProductRepo.findByRefId(content.getContentId());
			if (weatherProductList != null && weatherProductList.size() > 0) {
				for (WeatherProduct wtP : weatherProductList) {
//						wt = repo.findByWeatherId(wtP.getWeatherId());
					wt = weatherMapper.getWeather(wtP.getWeatherId());
					if (wt != null) {
						wt.setTypeName();
						wtList.add(wt);
					}
				}
			}
		}
		return wtList;
	}
	public int getWeatherListCnt(WeatherCondition condition) {
		String pdName = condition.getProductName();
		if(!"".equals(pdName)&&pdName!=null) {
			return getWeatherListByProductName(pdName).size();
		}
		return weatherMapper.getWeatherListCnt(condition);
	}
	
	
	public boolean addProductInWeather(String weathercode,String productSeq) {
		return weatherMapper.addProductInWeather(weathercode,productSeq)==1;
	}
	
	public Weather getWeatherByWeatherId(String weatherId) {
		WeatherCondition wt = new WeatherCondition();
		wt.setWeatherId(weatherId);
		
//				repo.findByWeatherId(weatherId);
		List<Weather> wtList = weatherMapper.getWeatherList(wt);
		if(wtList.size()>0) {
			return wtList.get(0);
		}
		return null;
	}
	
	public List<Content> getProductListByWeather(Weather weather){
		List<Content> contentList = weatherMapper.getProductListByWeather(weather);
		for (Content ct : contentList) {
			if (ct.isMainImg()) {
				ct.setMainUrl(contentService.getUrl(ct));
			} else {
				ct.setMainUrl(contentService.NO_CONTENT_IMG_PATH);
			}
		}
		return contentList;
	}
	
	
	public List<Content> deleteWeatherProduct(String weatherId, String productId, String userId) {
		Weather wt = new Weather();
		wt.setWeatherId(weatherId);
		wt.setRefId(productId);
		
		WeatherProduct product = weatherProductRepo.findByWeatherIdAndRefId(weatherId, productId);
		product.setDelYn("Y");
		product.setBasicInfo(userId);
		weatherProductRepo.save(product);
		
		List<Content> productList = getProductListByWeather(wt);
		
		return productList;
		
	}
	

	public List<Content> createWeatherProduct(String weatherId, List<String> productIds, String userId) {
		List<Content> productList  = new ArrayList<>();
		WeatherProduct product = null;
		for (String productId : productIds) {
			product = new WeatherProduct();
			product.setWeatherId(weatherId);
			product.setPriority(YesNo.NO.getVal());
			product.setRefId(productId);
			product.setBasicInfo(userId);
			weatherProductRepo.save(product);
		}
		Weather wt = new Weather();
		wt.setWeatherId(weatherId);
		productList = getProductListByWeather(wt);
		return productList;
	}

	private int getRandom(int size) {
		String date = CommonUtil.getDate();
		if(size > 9) {
			return CommonUtil.getRandomInt(size, date);
		}else {
			int day = Integer.parseInt(date.substring(4));
			return day%size;
		}
	}

	public List<Content> modifyProduct(String weatherId, String productId, String modProductId, String userId) {
		List<Content> productList  = new ArrayList<>();
		WeatherProduct product = null;
		product = weatherProductRepo.findByWeatherIdAndRefId(weatherId,productId);
		WeatherProduct neoProduct = null;
		neoProduct = weatherProductRepo.findByWeatherIdAndRefId(weatherId,modProductId);
		if(neoProduct!=null) { // 바뀔려는 상품이 이미 데이터에 있으면 서로 del값만 바꿔준다
			neoProduct.setDelYn("N");
			neoProduct.setBasicInfo(userId);
			product.setDelYn("Y");
			product.setBasicInfo(userId);
			
			weatherProductRepo.save(neoProduct);
			weatherProductRepo.save(product);
		}else {
			product.setRefId(modProductId);
			product.setBasicInfo(userId);
			weatherProductRepo.save(product);
		}
		
		
		Weather wt = new Weather();
		wt.setWeatherId(weatherId);
		productList = getProductListByWeather(wt);
		return productList;
	}

	public List<Content> updatePriority(String weatherId, String productId, String userId,String yesOrNo) {
		
			Weather wt = new Weather();
			wt.setWeatherId(weatherId);
			wt.setRefId(productId);
			
			WeatherProduct product = weatherProductRepo.findByWeatherIdAndRefId(weatherId, productId);
			product.setPriority(yesOrNo);
			product.setBasicInfo(userId);
			weatherProductRepo.save(product);
			
			List<Content> productList = getProductListByWeather(wt);
			
			return productList;
			
	}

	public void saveWeather(Weather weather) {
		repo.save(weather);
	}
		
}
