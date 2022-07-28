package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.PointUseGetType;
import com.kbeauty.gbt.entity.enums.PointUseGetTypeDetail;
import com.kbeauty.gbt.entity.enums.UserRole;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "GAMELOG")
@Data
@EqualsAndHashCode(callSuper = false)
public class GameLog extends CommonDomain {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq;	
	@Column(name = "userid")	    private String userId;
	@Column(name = "stage") 		private int stage;
	@Column(name = "status") 		private int status;
	@Column(name = "cleardate")		private String clearDate;
	@Column(name = "cleartime")		private String clearTime;
	@Column(name = "clearmoment")	private String clearMoment;
	
	public boolean isEmpty() {
		return  StringUtil.isEmpty(userId) ||
				stage<1;
	}
	
	public void setGameLogOnData(GameData data) {
		userId = data.getUserId();
		stage = data.getStage();
		status = data.getStatus();
		clearDate = data.getClearDate();
		clearTime = data.getClearTime();
		clearMoment  = data.getClearMoment();
	}
	
	public void setClearMoment() {
		String dateStr = CommonUtil.getSysTime();
		clearDate = dateStr.substring(0, 8);
		clearTime = dateStr.substring(8);
		dateStr =dateStr.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", "");
		clearMoment = dateStr;
	}
}
