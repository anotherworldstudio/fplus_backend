package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Exp;

@Repository
public interface ExpRepo extends CrudRepository<Exp, Long>{
	
	List<Exp> findByUserId(String userId);

	Exp findByExpId(String expId);
	
}
