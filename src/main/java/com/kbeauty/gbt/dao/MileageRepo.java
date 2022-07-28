package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Mileage;

@Repository
public interface MileageRepo extends CrudRepository<Mileage, Long>{
	
	List<Mileage> findByUserId(String userId);
	Mileage findByMileId(String mileId);
	
}
