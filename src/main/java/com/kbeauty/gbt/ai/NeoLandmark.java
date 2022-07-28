package com.kbeauty.gbt.ai;

public class NeoLandmark {

	String type;
	
	Point point;
	
	public NeoLandmark(String type, Point point) {
		super();
		this.type = type;
		this.point = point;
	}
	
	public NeoLandmark(String type, float x, float y) {
		this(type, new Point(x, y));
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public int getX() {
		return this.point.getX();
	}
	
	public int getY() {
		return this.point.getY();
	}
	
	@Override
	public String toString() {
		return "NeoLandmark [type=" + type + ", point=" + point + "]"+"\n";
	}
	
	
}
