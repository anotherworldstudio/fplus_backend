package com.kbeauty.gbt.ai;

import com.kbeauty.gbt.entity.CommonConstants;

public class ImgCrop {
	
	public final static String RC = "RIGHT_CENTER";
	public final static String LC = "LEFT_CENTER";
	
	public final static String BS = "BOTTOM_CENTER";
	public final static String CT = "CENTER";
	public final static String TP = "TWO_POINTER";
	public final static String TR = "TWO_CENTER_R";
	public final static String TL = "TWO_CENTER_L";
	
	private final static int AI_LEN = CommonConstants.AI_SIZE;
	private final static int DR_LEN = CommonConstants.TREATY_SIZE;
	
	private int x;
	private int y;
	private int width;
	private int height;
	
	
	private double rate;	
	boolean isNormal; //일반적으로 얼굴의 길이가 가로 보다 길다.
	
	private ImgCrop child;
	
	public ImgCrop(Point p, int width, int height) {
		this.x = p.getX();
		this.y = p.getY();
		this.width = width;
		this.height = height;
	}
		
	public ImgCrop(int width, int height) {
		this.x = 0;
		this.y = 0;
		this.width = width;
		this.height = height;		
		
		if(height > width) {	
			isNormal = true;
			this.rate = width / (double)height;
		}else {
			isNormal = true;
			this.rate = height / (double)width;
		}
	}
	
	public void setChild() {		
		int width = 0; 
		int height = 0;
		if(isNormal) {			
			if(this.height <=  AI_LEN) {
				child = new ImgCrop(this.width, this.height);
			}else {
				width = (int) (AI_LEN * this.rate);
				if( ! isEven(width) ) {
					width++;
				}
				height = AI_LEN;
				child = new ImgCrop(width, height);
			}
		}else {			
			if(this.width <=  AI_LEN) {
				child = new ImgCrop(this.width, this.height);
			}else {
				width  = AI_LEN;
				height = (int) (AI_LEN * this.rate);
				if( ! isEven(height) ) {
					height++;
				}
				child = new ImgCrop(width, height);
			}
		}
	}
	
	public ImgCrop crop(int x, int y, String type) {
		return crop(new Point(x, y), type);
	}
	
	public ImgCrop crop(Point p1, Point p2, String type) {
		Point p = null;
		if(ImgCrop.TP.equals(type)) {
			p = new Point(p1.getX(), p2.getY());
			return  crop(p, ImgCrop.BS);
		}else if(ImgCrop.TR.equals(type)) {
			p = new Point(p2.getX(), p1.getY());
			return  crop(p, type);
		}else if(ImgCrop.TL.equals(type)) {			
			p = new Point(p2.getX(), p1.getY());
			return  crop(p, type);
		}
		
		return null;
	}
	
	public ImgCrop crop(Point p, String type) {
		int x = 0;
		int y = 0;
		int width = this.child.getWidth();
		int height = this.child.getHeight();
		ImgCrop result = null;		
		if(BS.equals(type)) { // 256 안맞춤
			x = p.getX() - child.getHWidth();
			y = p.getY() - child.getHeight();	
			
			if(x < 0) {
				width = width + x;
			}
			
			if(y < 0) {
				height = height + y;
			}
			
			result = new ImgCrop(new Point(x,y), width, height);			
		}else if(CT.equals(type)) { // 256 안맞춤
			x = p.getX() - child.getHWidth();
			y = p.getY() - child.getHHeight();
			
			result = new ImgCrop(new Point(x,y), width, height);			
		}else if(TL.equals(type)) { // 256 안맞춤
			x = p.getX() - child.getWidth();
			y = p.getY();			
			result = new ImgCrop(new Point(x,y), width, height);
		}else if(TR.equals(type)) { // 256 안맞춤			
			result = new ImgCrop(p, width, height);
		}else if(RC.equals(type)) { // 256 안맞춤
			x = p.getX() - child.getWidth();
			y = p.getY() - child.getHHeight();
			
			if(x < 0) {
				width = width + x;
			}
			
			if(y < 0) {
				height = height + y;
			}
			result = new ImgCrop(new Point(x,y), width, height);
		}else if(LC.equals(type)) { // 256 안맞춤
			x = p.getX();
			y = p.getY() - child.getHHeight();
			
			if(x < 0) {
				width = width + x;
			}
			
			if(y < 0) {
				height = height + y;
			}
			result = new ImgCrop(new Point(x,y), width, height);
		}
				
		log("Before : " + result.toString());
		return getVaildData(result);
	}
	
	private ImgCrop getVaildData(ImgCrop child) {		
		if(this.width * this.height <  child.width * child.height) {
			return copy();
		}
		
		if(child.getX() > this.getRWidth()) { 
			return copy();
		}
		
		if(child.getY() > this.getRHeight()) {			
			return copy();
		}
				
		int widthGap = child.getRWidth() - this.getRWidth();		
		if(widthGap > 0) { // child가 더 넓다. 
			child.addWidth(widthGap * -1);
		}
		
		int heightGap = child.getRHeight() - this.getRHeight();		
		if(heightGap > 0) { // child가 더 넓다. 
			child.addHeight(heightGap * -1);
		}		
		log("After : " + child.toString());
		return child;
	}
	
	private ImgCrop copy() {
		return new ImgCrop(this.width, this.height);
	}
	
	private void addWidth(int gap) {
		this.width = this.width + gap;		
	}
	
	private void addHeight(int gap) {
		this.height = this.height + gap;
	}
	
	private int getRWidth() {
		return this.x + this.width;
	}
	
	private int getHWidth() {
		return this.width / 2;
	}
	
	private int getHHeight() {
		return this.height / 2;
	}
	
	private int getRHeight() {
		return this.y + this.height;
	}
	
	private boolean isEven(int n) {
        return (n & 1) == 0;
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

	public void log(String str) {
		System.out.println(str);
	}
	
	
	@Override
	public String toString() {
		return "ImgCrop [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
	}

	public static void main(String[] args) {
		
		ImgCrop crop = new ImgCrop(722, 768);
		crop.setChild();
		ImgCrop child = null;
		
//		child = crop.crop(283, 235, ImgCrop.BS);
//		System.out.println(child);
//		
//		child = crop.crop(361, 333, ImgCrop.CT);
//		System.out.println(child);
		Point p1 = null; 
        Point p2 = null;
				
//		p1 = new Point(232,331);
//		p2 = new Point(373,480);
//		child = crop.crop(p1, p2, ImgCrop.TP);
//		System.out.println(child);
//		
//		p1 = new Point(482,305);
//		p2 = new Point(373,480);
//		child = crop.crop(p1, p2, ImgCrop.TP);
//		System.out.println(child);
		
        
		p1 = new Point(232,331);
		p2 = new Point(373,480);
		child = crop.crop(p1, p2, ImgCrop.TR);
		System.out.println(child);
		
		p1 = new Point(482,317);
		p2 = new Point(373,480);
		child = crop.crop(p1, p2, ImgCrop.TL);
		System.out.println(child);
		
	}

}
