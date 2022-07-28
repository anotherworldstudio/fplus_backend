package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.User;

@Repository
public interface UserRepo extends CrudRepository<User, Long>{
	
	User findByEmail(String email);
	User findByEmailAndOauthType(String email, String oauthType);
	User findByUserAppleKeyAndOauthType(String userAppleKey, String oauthType);
	User findByUserId(String userId);
	List<User> findByUserName(String userName);
	
}
