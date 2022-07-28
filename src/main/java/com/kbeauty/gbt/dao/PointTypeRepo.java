package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Exp;
import com.kbeauty.gbt.entity.domain.PointType;

@Repository
public interface PointTypeRepo extends CrudRepository<PointType, Long>{
	
	PointType findBySystemType(String systemType);
	PointType findByPointTypeId(String pointTypeId);
	List<PointType> findByBigType(String bigType);
}
