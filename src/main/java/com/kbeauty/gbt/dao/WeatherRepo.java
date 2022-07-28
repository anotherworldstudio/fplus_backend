package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.Weather;

@Repository
public interface WeatherRepo extends CrudRepository<Weather, Long>{
	
	Weather findByWeatherCode(String weatherCode);
	Weather findByWeatherId(String weatherId);
	

}
