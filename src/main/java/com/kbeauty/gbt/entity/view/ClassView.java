package com.kbeauty.gbt.entity.view;

import java.util.List;

import lombok.Data;

@Data
public class ClassView extends CommonView{
	
	private String classId;
	private String classType;
	private String className;
	private String level;
	private String middleId;
	private String middleName;
	private String subId;
	private String subName;
	
	private List<ClassView> subClassList;
	 
}
