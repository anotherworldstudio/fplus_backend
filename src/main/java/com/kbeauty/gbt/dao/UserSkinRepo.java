package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.UserSkin;

@Repository
public interface UserSkinRepo extends CrudRepository<UserSkin, Long>{
	
	UserSkin findByUserId(String userId);
	
	
}
