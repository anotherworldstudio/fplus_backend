package com.kbeauty.gbt.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinRanking;
import com.kbeauty.gbt.entity.domain.SkinResource;
import com.kbeauty.gbt.entity.domain.SkinScore;
import com.kbeauty.gbt.entity.enums.UserSkinStatus;
import com.kbeauty.gbt.entity.view.SkinResult;

@Repository
public interface SkinRankingRepo  extends CrudRepository<SkinRanking, Long>{
	
	SkinRanking findByUserId(String userId);	

}

