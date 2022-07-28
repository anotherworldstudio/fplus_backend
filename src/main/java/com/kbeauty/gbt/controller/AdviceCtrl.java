package com.kbeauty.gbt.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kbeauty.gbt.entity.view.CommonView;
import com.kbeauty.gbt.exception.MessageException;

@RestControllerAdvice
public class AdviceCtrl {
	
	@ExceptionHandler(MessageException.class) 
	public CommonView jsonException(MessageException ex) {
        return new CommonView(ex);
    }	
	
	
}
