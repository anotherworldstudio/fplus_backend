package com.kbeauty.gbt.entity.view;

import java.util.ArrayList;
import java.util.List;

import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;

@Data
public class DiaryView  extends CommonView{
	
	private String processDate;
	private int processCnt;	
	private String skinId;
	private List<SkinResultView> skinResultViewList;
	
	public DiaryView() {
		skinResultViewList = new ArrayList<SkinResultView>();
	}
	
	public DiaryView add(SkinResultView skinResultView) {
		skinResultViewList.add(skinResultView);
		return this;
	}
}
