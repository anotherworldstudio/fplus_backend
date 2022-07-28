package com.kbeauty.gbt.entity.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.entity.enums.YesNo;
import com.kbeauty.gbt.util.CommonUtil;

import lombok.Data;

@MappedSuperclass
@Data
public class CommonDomain implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	private String code;

	@Transient
	private String msg;

	@Column(name = "regdate", updatable = false)
	private String regDate;
	@Column(name = "regtime", updatable = false)
	private String regTime;
	@Column(name = "reguser", updatable = false)
	private String regUser;

	@Column(name = "chgdate")
	private String chgDate;
	@Column(name = "chgTime")
	private String chgTime;
	@Column(name = "chgUser")
	private String chgUser;

	@Column(name = "delyn")
	private String delYn;

	public void setBasicInfo(CommonDomain basicInfo) {
		regDate = basicInfo.getRegDate();
		regTime = basicInfo.getRegTime();
		regUser = basicInfo.getRegUser();

		chgDate = basicInfo.getChgDate();
		chgTime = basicInfo.getChgTime();
		chgUser = basicInfo.getChgUser();
	}

	public void setBasicInfo(String dateStr, String userId) {
		regDate = dateStr.substring(0, 8);
		regTime = dateStr.substring(8);
		regUser = userId;

		chgDate = dateStr.substring(0, 8);
		chgTime = dateStr.substring(8);
		chgUser = userId;
	}

	public void setBasicInfo(String userId) {
		String dateStr = CommonUtil.getSysTime();

		regDate = dateStr.substring(0, 8);
		regTime = dateStr.substring(8);
		regUser = userId;

		chgDate = dateStr.substring(0, 8);
		chgTime = dateStr.substring(8);
		chgUser = userId;
	}
	
	public void setBasicInfo() {
		String dateStr = CommonUtil.getSysTime();

		regDate = dateStr.substring(0, 8);
		regTime = dateStr.substring(8);

		chgDate = dateStr.substring(0, 8);
		chgTime = dateStr.substring(8);
	}


	public void setDelete() {
		this.delYn = YesNo.YES.getVal();
	}

	public boolean isDelete() {
		return YesNo.YES.getVal().equals(delYn);
	}

	public boolean isOk() {
		return "0000".equals(code);
	}

	public void setOk() {
		this.code = "0000";
		this.msg = "";
	}

	public void setError(ErrMsg msg) {
		this.code = msg.getCode();
		this.msg = msg.getMsg();
	}

}
