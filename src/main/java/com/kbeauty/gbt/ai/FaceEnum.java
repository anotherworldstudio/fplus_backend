package com.kbeauty.gbt.ai;

public enum FaceEnum {
	
	FORHEAD_LEFT("W1", "01_이마왼쪽"),
	FORHEAD_MID("W2", "02_이마중간"),
	FORHEAD_RIGHT("W3", "03_이마오른쪽"),
	GLABELLA("W4", "04_미간"),
	EYE_LEFT("W5", "05_왼쪽눈주위"),
	EYE_RIGHT("W6", "06_오른쪽눈주위"),
	MOUTH("W7", "07_입주위"),
	CHEEK_LEFT("W8", "08_왼쪽팔자"),
	CHEEK_RIGHT("W9", "09_오른쪽팔자"),
	TZONE_TOP("T1", "10_T-Zone상단"),
	TZONE_BOTTOM("T2", "11_T-Zone하단"),
	UZONE_LEFT("U1", "12_U-Zone왼쪽"),
	UZONE_RIGHT("U2", "13_U-Zone오른쪽"),
	MOUTH_LEFT("M1", "14_좌측입주위"),
	MOUTH_RIGHT("M2", "15_우측입주위")
	;
	
	private String fileFix;
	private String hanFix;
	
	
	private FaceEnum(String fileFix, String hanFix) {
		this.hanFix = hanFix;
		this.fileFix = fileFix;
	}
	
	public String getFileFix() {
		return this.fileFix;
	}
	
	public String getHanFix() {
		return this.hanFix;
	}
	
}
