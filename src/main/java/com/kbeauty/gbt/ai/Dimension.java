package com.kbeauty.gbt.ai;

import java.util.ArrayList;
import java.util.List;

public class Dimension {
	private int x;
	private int y;
	private int width;
	private int height;
	
	
	public Dimension(int x, int y, int width, int height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public List<Point> getPointList(){
		List<Point> list = new ArrayList<>();
		Point p = null;
		p = new Point(this.x, this.y);
		list.add(p);
		p = new Point(this.x+this.width, this.y);
		list.add(p);
		p = new Point(this.x+this.width, this.y+this.height);
		list.add(p);
		p = new Point(this.x, this.y+this.height);
		list.add(p);
		return list;
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
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}


	@Override
	public String toString() {
		return "Dimension [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}
	

}
