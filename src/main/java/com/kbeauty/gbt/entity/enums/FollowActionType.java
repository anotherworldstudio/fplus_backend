package com.kbeauty.gbt.entity.enums;

public enum FollowActionType implements CodeVal{
	
	REQUEST("1000","Request"),
	WITHDRAW("2000", "Withdraw"),
	UNFOLLOWING("3000","Unfollowing"),
	DELETE("4000","Delete"),
	CONFIRM("0000","Confirm"),
	CANCEL_WITHDRAW("2100","Cancel_Unfollowing"),
	CANCEL_UNFOLLOWING("3100","Cancel_Unfollowing"),
	CANCEL_DELETE("4100","Cancel_Delete"),
	BLOCK("5000","Block"),
	CANCEL_BLOCK("5100","Cancel_Block");
	
		
	private String code;
	private String val;
	
	FollowActionType(String code, String val){
		this.code = code;
		this.val = val;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	
}
