package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.entity.domain.Skin;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.SkinScore;

import lombok.Data;

@Data
public class UserSimpleSkinView  extends CommonView{
	private List<Skin> skinList;
	
}
