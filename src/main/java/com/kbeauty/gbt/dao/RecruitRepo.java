package com.kbeauty.gbt.dao;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Recruit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitRepo extends CrudRepository<Recruit, Long>{
	Recruit findByRecruitId(String recruitId);
//	공고 아이디로 찾기
	Recruit findByPath(String path);
//	Path로 찾기
	Recruit findBySeq(int seq);
//	고유번호로 찾기
	List<Recruit> findByRecruitType(String recruitType);
//	공고 타입으로 찾기
}
