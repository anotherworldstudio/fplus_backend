package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Others;
import com.kbeauty.gbt.entity.domain.Weather;

import lombok.Data;

@Data
public class NoticeView extends CommonView {
	
	private List<ContentView> noticeList;
	private int count;
}
