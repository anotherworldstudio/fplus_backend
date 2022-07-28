package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinInfo;

@Repository
public interface SkinInfoRepo  extends CrudRepository<SkinInfo, Long>{
	List<SkinInfo> findBySkinId(String skinId);
}
