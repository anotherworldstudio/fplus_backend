package com.kbeauty.gbt.entity.view;

import com.kbeauty.gbt.entity.domain.*;
import lombok.Data;

import java.util.List;

@Data
public class FplusUserView extends CommonView{
	private User2 user;
	private String email;
	private String password;
}
