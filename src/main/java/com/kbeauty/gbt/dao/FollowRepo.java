package com.kbeauty.gbt.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Price;

@Repository
public interface FollowRepo   extends CrudRepository<Follow, Long>{
		 
	Follow findByFollowId(String followId);
}
