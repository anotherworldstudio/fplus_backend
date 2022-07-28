package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.LessonFace;

@Repository
public interface LessonFaceRepo   extends CrudRepository<LessonFace, String>{
	LessonFace findByLessonFaceId(String lessonFaceId);
	List<LessonFace> findByLessonId(String lessonId);
	
	
}
