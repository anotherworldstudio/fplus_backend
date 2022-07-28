package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinLevel;

@Repository
public interface SkinLevelRepo  extends CrudRepository<SkinLevel, Long>{
	
}
