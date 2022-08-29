package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.domain.*;
import com.kbeauty.gbt.entity.enums.OtherType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Data
public class RecruitView extends CommonView {
	private Recruit recruit;
	private List<Resources> resourceList;
	private List<Resources> hashList;


}