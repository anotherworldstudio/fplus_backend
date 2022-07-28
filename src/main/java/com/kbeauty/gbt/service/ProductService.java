package com.kbeauty.gbt.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kbeauty.gbt.dao.ContentMapper;
import com.kbeauty.gbt.entity.enums.ClassType;
import com.kbeauty.gbt.entity.view.ClassView;
import com.kbeauty.gbt.util.StringUtil;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ProductService {
	
	@Autowired
	private ContentMapper contentMapper;

	
	public List<ClassView> getClassList(String type) {
		ClassView searchView = new ClassView();
		searchView.setClassType(type);		
		List<ClassView> rawClassList = contentMapper.getClassList(searchView);
		List<ClassView> resultList = new ArrayList<ClassView>();
		
		
		ClassView classView = null;
		ClassView subClassView = null;
		List<ClassView> subClassList = null;
		String middleId = null;
		for (ClassView rawClass : rawClassList) {
			if(classView == null) {
				classView = new ClassView();
				subClassList = new ArrayList<ClassView>();
				middleId = rawClass.getMiddleId();
				classView.setMiddleId(middleId);
				classView.setMiddleName(rawClass.getMiddleName());
				
			}else if(! rawClass.getMiddleId().equals(middleId)) {	
				classView.setSubClassList(subClassList);
				resultList.add(classView);
				
				classView = new ClassView();
				subClassList = new ArrayList<ClassView>();
				middleId = rawClass.getMiddleId();
				classView.setMiddleId(middleId);
				classView.setMiddleName(rawClass.getMiddleName());
				
			}
			
			subClassView  = new ClassView();
			subClassView.setSubId(rawClass.getSubId());
			subClassView.setSubName(rawClass.getSubName());
			subClassList.add(subClassView);			
		}
		
		if( ! StringUtil.isEmpty(subClassList) ) {			
			classView.setSubClassList(subClassList);
			resultList.add(classView);
		}
		
		return resultList;
	}
	
	

}
