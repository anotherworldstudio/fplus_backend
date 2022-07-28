package com.kbeauty.gbt.entity.view;

import java.io.Serializable;

import com.kbeauty.gbt.entity.enums.ErrMsg;
import com.kbeauty.gbt.exception.MessageException;
import com.kbeauty.gbt.util.StringUtil;

import lombok.Data;
   
@Data
public class CommonView  implements Serializable{	
	
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String msg;
	
	public CommonView() {
		super();
	}
	
	public CommonView(MessageException ex) {
		this();
		this.code = ex.getMsg().getCode();
		this.msg = ex.getMsg().getMsg();
	}
	
	public void setOk() {
		this.code = "0000";
		this.msg = "";		
	}
	
	public void setError(ErrMsg msg) {
		this.code = msg.getCode();
		this.msg = msg.getMsg();
	}
	
	/**
	 * 확실하게 성공 인지 여부 
	 * @return
	 */
	public boolean isOk() {
		return "0000".equals(code);
	}
	
	/**
	 * 확실하게 오류인지 여부 
	 * @return
	 */
	public boolean isNotOk() {
		if(StringUtil.isEmpty(code)) {
			return false;
		}
		
		return ! "0000".equals(code);
	}
}
