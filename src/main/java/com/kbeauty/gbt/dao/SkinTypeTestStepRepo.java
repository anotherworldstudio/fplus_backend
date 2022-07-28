package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.SkinTypeTestStep;


@Repository
public interface SkinTypeTestStepRepo  extends CrudRepository<SkinTypeTestStep, Long>{
	List<SkinTypeTestStep> findByTestId(String testId);	
	SkinTypeTestStep findByTestIdAndStep(String testId,int step);	
	SkinTypeTestStep findByStepId(String stepId);
	
}
