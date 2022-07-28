package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.Price;

@Repository
public interface CountRepo   extends CrudRepository<Count, String>{
	Count findByContentId(String contentId);
}
