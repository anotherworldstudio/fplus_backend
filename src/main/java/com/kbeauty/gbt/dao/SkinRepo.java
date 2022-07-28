package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Skin;

@Repository
public interface SkinRepo  extends CrudRepository<Skin, Long>{
	Skin findBySkinId(String skinId);	
	
	
}
