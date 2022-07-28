package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.Weather;
import com.kbeauty.gbt.entity.domain.WeatherProduct;

@Repository
public interface WeatherProductRepo extends CrudRepository<WeatherProduct, Long>{
	
	WeatherProduct findByWeatherId(String weatherId);
	WeatherProduct findByWeatherIdAndRefId(String weatherId,String refId);
	List<WeatherProduct> findByRefId(String refId);
}
