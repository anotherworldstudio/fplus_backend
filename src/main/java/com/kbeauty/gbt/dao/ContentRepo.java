package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.view.AiRecommandProduct;

@Repository
public interface ContentRepo  extends CrudRepository<Content, Long>{
	Content findByContentId(String contentId);
	Content findByTitle(String title);
	Content findByPath(String path);
	Content findBySeq(int seq);
	List<Content> findByContentType(String contenttype);
}
