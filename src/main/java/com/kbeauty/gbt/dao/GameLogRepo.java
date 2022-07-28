package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.GameData;
import com.kbeauty.gbt.entity.domain.GameLog;

@Repository
public interface GameLogRepo extends CrudRepository<GameLog, Long> {

	GameLog findByuserId(String userId);
	List<GameLog> findByStage(int stage);
	List<GameLog> findByStageAndStatus(int stage, int status);

}
