package com.kbeauty.gbt.entity.view;

import java.util.List;

import com.kbeauty.gbt.entity.domain.AppleLogin;
import com.kbeauty.gbt.entity.domain.User;

import lombok.Data;

@Data
public class AppleLoginView extends CommonView{
	
	private User user;
	private AppleLogin appleLogin;
	
}
