package com.kbeauty.gbt.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kbeauty.gbt.entity.Paging;
import com.kbeauty.gbt.entity.PagingList;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Login;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.entity.view.WeatherCondition;
import com.kbeauty.gbt.entity.view.WeatherDetailView;
import com.kbeauty.gbt.service.WeatherService;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/w1/weather")
@Controller
@Slf4j
public class WeatherCtrl {

	@Autowired
	WeatherService service;	

	@Resource
	private Login login;

	private final static String conditionKey = "WeatherCondiction";
	private final static String CONTENT_VIEW = "contentView";
	
	private void setCondition(HttpServletRequest request, WeatherCondition condition) {
		//TODO 기존에 conditionKey를 다 삭제한다.
		HttpSession session = request.getSession();
		session.setAttribute(conditionKey, condition);
	}
	
	private WeatherCondition getCondition(HttpServletRequest request) {
		HttpSession session = request.getSession();
		WeatherCondition condiiton = (WeatherCondition)session.getAttribute(conditionKey);
		return condiiton;
	}
	
	private WeatherCondition getDefaultContentCondition() {
		WeatherCondition condition = new WeatherCondition();
//		condition.setContentType(ContentType.PRODUCT.getCode());
		
		return condition;
	}
	
	private void goListCommon(HttpServletRequest request, WeatherCondition condition, Model model) {
		String reset = (String)request.getParameter("reset");
		if(YesNo.isYes(reset)) {
			condition = getDefaultContentCondition();						
		}else {			
			if(condition == null || condition.isEmpty()) {			
				condition = getCondition(request);	
				if(condition == null) {
					condition = getDefaultContentCondition();
				}		
			}
		}
		
		
		model.addAttribute("condition", condition);
	}			

	
	@GetMapping("/go_list")
	public String goList(HttpServletRequest request, WeatherCondition condition, Model model) {		
		
		goListCommon(request, condition, model);
		
		return "admin/weather/list";
	}
	
	@RequestMapping(value="/list")
	@ResponseBody
	public PagingList<Weather> getWeatherListView(HttpServletRequest request, @RequestBody WeatherCondition condition) {
		
		PagingList<Weather> list = new PagingList<>();
		if(condition == null) condition = new WeatherCondition();
		Paging paging = new Paging();
		paging.setCondition(condition);
		
		int perPageNum = condition.getPerPageNum();
		if(perPageNum == 0) {
			perPageNum = 10;
		}
		paging.setDisplayPageNum(perPageNum);
		
		int currPage = condition.getPage();
		if(currPage == 0) {
			currPage = 1;
		}
		paging.setPage(currPage);

		setCondition(request, condition);
		
		String userId = login.getUserId();
		condition.setSearchUserid(userId);
		
		List<Weather> weatherList = service.getWeatherList(condition);
		if(weatherList == null || weatherList.isEmpty()) {
			list.setError(ErrMsg.NO_RESULT);			
			return list;
		}
//		for (Weather weather : weatherList) {
//			weather.setTypeName(WeatherType.);
//		}
		int totalCnt = service.getWeatherListCnt(condition);
		paging.setTotalCount(totalCnt);
		
		list.setList(weatherList);
		list.setPaging(paging);
		list.setOk();
		
		return list;
	}	
	
	@RequestMapping(value="/detail/{weatherId}", method=RequestMethod.GET)
	public String getContentView(			
			@PathVariable("weatherId") String weatherId,
			Model model
			) { 
		
		String userId = login.getUserId();
		WeatherDetailView weatherView = null;
		Weather wt = service.getWeatherByWeatherId(weatherId);
		if(wt!=null) {
		weatherView = new WeatherDetailView();
		weatherView.setWeather(wt);
		weatherView.setProductList(service.getProductListByWeather(wt));
		}
		if(weatherView == null) {
			weatherView = new WeatherDetailView();
			weatherView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		weatherView.setOk();
		
		model.addAttribute("weatherView", weatherView);
		return "admin/weather/detail"; 
		
	}
	
	
	@RequestMapping(value="/save_weather", method = RequestMethod.POST)
	public String saveWeather(HttpServletRequest request, WeatherDetailView inputWeatherView,Model model){
		Weather weather = inputWeatherView.getWeather();
		String userId = login.getUserId();
		weather.setBasicInfo(userId);
		service.saveWeather(weather);
		WeatherDetailView weatherView = null;
		Weather wt = service.getWeatherByWeatherId(weather.getWeatherId());
		if(wt!=null) {
		weatherView = new WeatherDetailView();
		weatherView.setWeather(wt);
		weatherView.setProductList(service.getProductListByWeather(wt));
		}
		if(weatherView == null) {
			weatherView = new WeatherDetailView();
			weatherView.setError(ErrMsg.NO_RESULT);
			return "";
		}
		weatherView.setOk();
		
		model.addAttribute("weatherView", weatherView);
		
		return "admin/weather/detail";
		
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/delete_product", method = RequestMethod.POST)
	public WeatherDetailView getProductByWeatherCode(@RequestParam(value = "weatherId") String weatherId,
			@RequestParam(value = "productId") String productId) throws Exception {

		WeatherDetailView weatherView = new WeatherDetailView();
		String userId = login.getUserId();

		List<Content> ctList = service.deleteWeatherProduct(weatherId, productId, userId);
		weatherView.setProductList(ctList);

		return weatherView;
	}

	@ResponseBody
	@RequestMapping(value = "/add_product", method = RequestMethod.POST)
	public WeatherDetailView addProduct(@RequestParam(value = "weatherId") String weatherId,
			@RequestParam(value = "productIds") List<String> productIds) throws Exception {

		WeatherDetailView view = new WeatherDetailView();

		String userId = login.getUserId();

		List<Content> productList = service.createWeatherProduct(weatherId, productIds, userId);

		view.setProductList(productList);
		view.setOk();

		return view;
	}

	@ResponseBody
	@RequestMapping(value="/mod_product", method=RequestMethod.POST)
	public WeatherDetailView modifiyPeerProduct(
			@RequestParam(value="weatherId") String weatherId,
			@RequestParam(value="modProductId")   String modProductId,
			@RequestParam(value="productId") String productId) throws Exception{
		
		WeatherDetailView view = new WeatherDetailView();
		String userId = login.getUserId();

		List<Content> productList = service.modifyProduct(weatherId, productId, modProductId, userId);

		view.setProductList(productList);
		view.setOk();

		return view;
	}
	
	@ResponseBody 
	@RequestMapping(value="/add_priority", method = RequestMethod.POST)
	public WeatherDetailView addPriority(
			@RequestParam(value="weatherId") String weatherId,
			@RequestParam(value="productId") String productId
			){
		WeatherDetailView view = new WeatherDetailView();
		String userId = login.getUserId();
		
		List<Content> productList = service.updatePriority(weatherId, productId, userId,"Y");
		
		view.setProductList(productList);
		view.setOk();
		
		return view;
		
	}
	
	@ResponseBody 
	@RequestMapping(value="/delete_priority", method = RequestMethod.POST)
	public WeatherDetailView deletePriority(
			@RequestParam(value="weatherId") String weatherId,
			@RequestParam(value="productId") String productId
			){
		WeatherDetailView view = new WeatherDetailView();
		String userId = login.getUserId();
		
		List<Content> productList = service.updatePriority(weatherId, productId, userId,"N");
		
		view.setProductList(productList);
		view.setOk();
		
		return view;
		
	}
	
}
