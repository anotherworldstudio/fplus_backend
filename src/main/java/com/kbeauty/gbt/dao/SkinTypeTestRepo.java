package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinTypeTest;
import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;


@Repository
public interface SkinTypeTestRepo extends CrudRepository<SkinTypeTest, Long>{
	SkinTypeTest findByTestId(String testId);
	List<SkinTypeTest> findByUserId (String userId);
	
}
