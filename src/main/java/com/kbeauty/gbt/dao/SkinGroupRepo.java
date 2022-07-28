package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinGroup;

@Repository
public interface SkinGroupRepo  extends CrudRepository<SkinGroup, Long>{
	
}
