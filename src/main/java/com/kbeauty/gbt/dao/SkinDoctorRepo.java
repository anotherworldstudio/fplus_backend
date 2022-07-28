package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinDoctor;

@Repository
public interface SkinDoctorRepo  extends CrudRepository<SkinDoctor, Long>{
	List<SkinDoctor> findBySkinId(String skinId);	
	
	
}
