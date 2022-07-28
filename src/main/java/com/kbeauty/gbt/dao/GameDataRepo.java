package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.GameData;


@Repository
public interface GameDataRepo   extends CrudRepository<GameData, Long>{
	
	GameData findByuserId(String userId);
	List<GameData> findByStage(int stage);
	List<GameData> findByStageAndStatus(int stage,int status);
	
}
