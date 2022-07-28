package com.kbeauty.gbt.entity;

import java.util.List;

import com.kbeauty.gbt.entity.view.CommonView;

import lombok.Data;

@Data
public class PagingList<T>  extends CommonView{
	private Paging paging;
	private List<T> list;
}
