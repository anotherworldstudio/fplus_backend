package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinResource;

@Repository
public interface SkinResourceRepo  extends CrudRepository<SkinResource, Long>{
	List<SkinResource> findBySkinId(String skinId);
}
