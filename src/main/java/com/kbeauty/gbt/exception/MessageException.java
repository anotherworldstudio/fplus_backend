package com.kbeauty.gbt.exception;

import com.kbeauty.gbt.entity.enums.ErrMsg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private ErrMsg msg;
	
	public MessageException(ErrMsg msg) {
		super(msg.getCode());
		this.msg = msg;
	}
	
}
