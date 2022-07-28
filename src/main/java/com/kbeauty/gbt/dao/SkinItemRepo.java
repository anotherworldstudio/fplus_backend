package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinItem;

@Repository
public interface SkinItemRepo  extends CrudRepository<SkinItem, Long>{
	
}
