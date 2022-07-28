package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Likes;

@Repository
public interface LikeRepo   extends CrudRepository<Likes, Long>{
	Likes findByLikeId(String likeId);
	
	List<Likes> findByContentId(String contentId, Sort sort);
}
