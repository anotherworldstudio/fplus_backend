package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.Training;

@Repository
public interface TrainingRepo extends CrudRepository<Training, String>{
	Training findByTrainingId(String trainingId);
}
