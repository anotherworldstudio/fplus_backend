package com.kbeauty.gbt.ai;

public class Point {
	private int x;
	private int y;
	
	public Point(double x, double y) {
		//일단 버림으로 처리 ==> 
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public Point(float x, float y) {
		//일단 버림으로 처리 ==> 
		this.x = (int)x;
		this.y = (int)y;
	}
	
	public Point(int x, int y) {
		if(x < 0) {
			x = 0;
		}
		
		if(y < 0) {
			y = 0;
		}		
		
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
	
}
