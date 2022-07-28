package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserFace;

@Repository
public interface UserFaceRepo extends CrudRepository<UserFace, Long>{
	
		UserFace findByUserFaceId(String userFaceId);
		UserFace findByUserIdAndWho(String userId, String who);
}
