package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class SkinResult  extends CommonView{
	
	private String itemCode; // 정보항목 코드 (주름/모공/여드름/색조/홍조) 
	private int score;
	private double orgScore;
	private String comment;
	private String append;

	private  ContentView prodContentView;
	private  ContentView treatContentView;	
	private  ContentView editorContentView;	
}
