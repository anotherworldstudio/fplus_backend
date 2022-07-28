package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Resources;

@Repository
public interface ResourceRepo   extends CrudRepository<Resources, String>{
	
	List<Resources> findByContentId(String contentId);
	Resources findByResourceId(String resourceId);
	
	List<Resources> findByContentId(String contentId, Sort sort);

}
