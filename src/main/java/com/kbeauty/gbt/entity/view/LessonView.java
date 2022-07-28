package com.kbeauty.gbt.entity.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Count;
import com.kbeauty.gbt.entity.domain.Follow;
import com.kbeauty.gbt.entity.domain.Lesson;
import com.kbeauty.gbt.entity.domain.LessonFace;
import com.kbeauty.gbt.entity.domain.Likes;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Price;
import com.kbeauty.gbt.entity.domain.Resources;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.enums.OtherType;

import lombok.Data;

@Data
public class LessonView extends CommonView implements Comparable<LessonView>{
	private long seq;
	private Lesson lesson;
	private Count count;
	private List<Resources> resourceList;
	private List<LessonFace> lessonFaceList;
	private List<LessonFace> skinToneList;
	private List<LessonFace> facialContourList;
	private List<LessonFace> seasonColorList;
	private List<LessonFace> genderList;
	private List<LessonFace> ageList;
	private List<LessonFace> typeList;
	private Training training;
	private int orders;
	private boolean skinTone;
	private boolean facialContour;
	private boolean seasonColor;
	private boolean gender;
	private boolean age;
	private boolean type;
	

	@Override
	public int compareTo(LessonView view) {
		if(view.orders < orders) {
			return 1;
		}else if (view.orders > orders) {
			return -1;
		}
		return 0;
	}
	


}