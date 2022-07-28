package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Others;

@Repository
public interface OthersRepo   extends CrudRepository<Others, String>{
	
	List<Others> findByContentId(String contentId);
	
	List<Others> findByContentIdAndOtherStatus(String contentId, String otherStatus);	
	
	List<Others> findByContentIdAndOtherTypeAndOtherStatus(String contentId, String otherType, String otherStatus);
	
	Others findByContentIdAndOtherId(String contentId, String otherId);
	
}
