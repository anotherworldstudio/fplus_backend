package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.ai.FaceEnum;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.Evaluate;
import com.kbeauty.gbt.entity.domain.FileInfo;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.Training;
import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;

import lombok.Data;

@Data
public class TrainingView  extends CommonView{
	
	private Training training;
	private List<Evaluate> evaluateList; 
	private User user;
	private User evaluateUser;
	private FileInfo beforeImgInfo;
	private FileInfo afterImgInfo;
	private int beforeheight;
	private int afterheight;
	
}
