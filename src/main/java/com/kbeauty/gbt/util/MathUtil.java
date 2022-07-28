package com.kbeauty.gbt.util;

public class MathUtil {
	
	public static float roundScore(float avgScore) {
		avgScore = avgScore * 10;
		int temp = Math.round(avgScore);
		avgScore = temp / 10f;
		return avgScore;
	}
	
	//testddddd

}
