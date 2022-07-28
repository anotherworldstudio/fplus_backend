package com.kbeauty.gbt.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.kbeauty.gbt.entity.domain.Lesson;

@Repository
public interface LessonRepo   extends CrudRepository<Lesson, String>{
	Lesson findByLessonId(String lessonId);
	
}
