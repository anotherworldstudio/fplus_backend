package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.ai.FaceEnum;
import com.kbeauty.gbt.entity.domain.Content;
import com.kbeauty.gbt.entity.domain.SkinInfo;
import com.kbeauty.gbt.entity.domain.UserFace;
import com.kbeauty.gbt.entity.enums.StoragePath;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.FileUtil;

import lombok.Data;

@Data
public class UserFaceView  extends CommonView{

	private UserFace userFace;
	private List<UserFace> list;
	private int count;
}
