package com.kbeauty.gbt.ai;

import java.util.ArrayList;
import java.util.List;

import com.kbeauty.gbt.entity.enums.Orientation;
import com.kbeauty.gbt.util.ImageUtil;

public class VisionFace {
	private int width;
	private int height;
	
	private Dimension dimension;
	private List<NeoLandmark> landmarks;	
	private NeoPose pose;
	
	private Orientation orientation;
	
	private String dir;
	private String baseName;
	private String ext;
	
	
	
	public VisionFace() {
		landmarks = new ArrayList<>();
	}
	
	public VisionFace(Dimension dimension, List<NeoLandmark> landmarks, NeoPose pose) {
		super();
		this.dimension = dimension;
		this.landmarks = landmarks;
		this.pose = pose;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public List<NeoLandmark> getLandmarks() {
		return landmarks;
	}

	public void setLandmarks(List<NeoLandmark> landmarks) {
		this.landmarks = landmarks;
	}

	public NeoPose getPose() {
		return pose;
	}

	public void setPose(NeoPose pose) {
		this.pose = pose;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
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

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "Face [dimension=" + dimension + ", landmarks=" + landmarks + ", pose=" + pose + "]";
	}
	
	public void adjustDemension(int chgWidth, int chgHeight) {
		int x = 0;
		int y = 0;
		
		int dX = dimension.getX();
		int dY = dimension.getY();
		
		int orgHeight = dimension.getHeight();
		double rate = chgHeight /(double) orgHeight ;
				
		for (NeoLandmark neoLandmark : landmarks) {
			x = neoLandmark.getX() - dX;
			x = (int)(x * rate); 
			y = neoLandmark.getY() - dY;
			y = (int)(y * rate);
			neoLandmark.setPoint(new Point(x,y));
		}
		
	}
	
	public Orientation genOrientation() {
		float roll = pose.getRoll();
		// 정상체크 해서 바로 리턴 
		if (roll > -50 && roll < 50) { 
			orientation = Orientation.R000;
			return orientation;
		}
		
		if (roll > 150) { // 뒤집힌 경우
			orientation = Orientation.R180;
		} else if (roll > 50) { // 우측			
			orientation = Orientation.R270; //270
		} else if (roll < -50) { // 좌측
			orientation = Orientation.R090;	// 90		 
		} else if (roll < -150) { // 뒤집힌 경우			
			orientation = Orientation.R180;
		}
		
		
		int x = 0;
		int y = 0;
		for (NeoLandmark neoLandmark : landmarks) {
			switch (orientation) {
			case R270: //270 
				x = neoLandmark.getY();
				y = width - neoLandmark.getX();
				break;				
			case R180:
				x = width - neoLandmark.getX();
				y = height - neoLandmark.getY();				
				break;
			case R090: // 90
				x = height - neoLandmark.getY();
				y = neoLandmark.getX();
				break;
			default:
				break;
			}
			neoLandmark.setPoint(new Point(x, y));
		}
		
		// demesion 정리
		x = dimension.getX();
		y = dimension.getY();
		int dWidth = dimension.getWidth(); //dimension width
		int dHeight = dimension.getHeight(); //dimension height
		
		switch (orientation) {
		case R270: //270
//			Point X  y
//			Point Y  전체 width – (x + w)
//			W  H
//			H  W
			
			x = y;                     
			y = width - ( dimension.getX() + dWidth); // x 로 바꾸지 마시오			
			dWidth = dHeight;
			dHeight = dWidth;
			break;				
		case R180:
//			Point X  전체 Width – ( x + w)
//			Point Y  전체 Height – ( y + h )
//			W  W 동일
//			H  H 동일
//			
			x = width  - ( x + dWidth  );
			y = height - ( y + dHeight );				
			break;
		case R090: // 90
//			Point X  전체 Height – ( y + h )
//			Point Y  x
//			W  H
//			H  W
			x = height - ( y + dHeight );
			y = dimension.getX();
			dWidth = dHeight;
			dHeight = dWidth;	
		default:
			break;
		}
		
		dimension = new Dimension(x, y, dWidth, dHeight);
		return orientation;
	}
	
	public NeoLandmark getLandmark(String type) {
		for (NeoLandmark neoLandmark : landmarks) {
			if(neoLandmark.getType().equals(type)) {
				return neoLandmark;
			}
		}
		return null;
	}
	
	public String getTempString() {
		StringBuffer sb = new StringBuffer();
		String line = null;
		for (NeoLandmark neoLandmark : landmarks) {			
			line = String.format("landmark=new NeoLandmark(\"%s\",%d,%d);landmarks.add(landmark);", neoLandmark.getType(), neoLandmark.getX(), neoLandmark.getY());
			sb.append(line).append("\n");
		}
		
		return sb.toString();
	}
	
	
	
	
	
	public VisionFace getTempNormalData() {
		dimension = new Dimension(86, 960, 3360, 3575);
		NeoLandmark landmark = null;
		landmark=new NeoLandmark("LEFT_EYE",1144,2485);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE",2367,2396);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_OF_LEFT_EYEBROW",699,2116);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_OF_LEFT_EYEBROW",1477,2106);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_OF_RIGHT_EYEBROW",2060,2073);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_OF_RIGHT_EYEBROW",2796,2040);landmarks.add(landmark);
		landmark=new NeoLandmark("MIDPOINT_BETWEEN_EYES",1798,2406);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_TIP",1848,3206);landmarks.add(landmark);
		landmark=new NeoLandmark("UPPER_LIP",1823,3681);landmarks.add(landmark);
		landmark=new NeoLandmark("LOWER_LIP",1867,4058);landmarks.add(landmark);
		landmark=new NeoLandmark("MOUTH_LEFT",1384,3857);landmarks.add(landmark);
		landmark=new NeoLandmark("MOUTH_RIGHT",2301,3788);landmarks.add(landmark);
		landmark=new NeoLandmark("MOUTH_CENTER",1847,3846);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_BOTTOM_RIGHT",2147,3328);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_BOTTOM_LEFT",1501,3320);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_BOTTOM_CENTER",1823,3418);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_TOP_BOUNDARY",1122,2361);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_RIGHT_CORNER",1382,2514);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_BOTTOM_BOUNDARY",1138,2584);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_LEFT_CORNER",900,2501);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_TOP_BOUNDARY",2383,2278);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_RIGHT_CORNER",2614,2409);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_BOTTOM_BOUNDARY",2383,2487);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_LEFT_CORNER",2136,2429);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYEBROW_UPPER_MIDPOINT",1090,1988);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYEBROW_UPPER_MIDPOINT",2427,1937);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EAR_TRAGION",458,2849);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EAR_TRAGION",3111,2713);landmarks.add(landmark);
		landmark=new NeoLandmark("FOREHEAD_GLABELLA",1785,2092);landmarks.add(landmark);
		landmark=new NeoLandmark("CHIN_GNATHION",1888,4511);landmarks.add(landmark);
		landmark=new NeoLandmark("CHIN_LEFT_GONION",832,3914);landmarks.add(landmark);
		landmark=new NeoLandmark("CHIN_RIGHT_GONION",2823,3799);landmarks.add(landmark);
		landmark=new NeoLandmark("UNRECOGNIZED",970,3350);landmarks.add(landmark);
		landmark=new NeoLandmark("UNRECOGNIZED",2617,3255);landmarks.add(landmark);
		
		pose = new NeoPose(1.7980561f, -2.8402402f, -3.8507023f);
		
				
		return this;
	}
	
	public VisionFace getTempAbNormalData() {
		dimension = new Dimension(1072, 27, 3545, 3448);
		NeoLandmark landmark = null;
		landmark=new NeoLandmark("LEFT_EYE",3162,1149);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE",3269,2339);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_OF_LEFT_EYEBROW",3508,755);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_OF_LEFT_EYEBROW",3565,1471);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_OF_RIGHT_EYEBROW",3586,2045);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_OF_RIGHT_EYEBROW",3667,2691);landmarks.add(landmark);
		landmark=new NeoLandmark("MIDPOINT_BETWEEN_EYES",3234,1790);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_TIP",2444,1844);landmarks.add(landmark);
		landmark=new NeoLandmark("UPPER_LIP",1997,1842);landmarks.add(landmark);
		landmark=new NeoLandmark("LOWER_LIP",1604,1865);landmarks.add(landmark);
		landmark=new NeoLandmark("MOUTH_LEFT",1811,1398);landmarks.add(landmark);
		landmark=new NeoLandmark("MOUTH_RIGHT",1904,2257);landmarks.add(landmark);
		landmark=new NeoLandmark("MOUTH_CENTER",1814,1849);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_BOTTOM_RIGHT",2338,2151);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_BOTTOM_LEFT",2337,1516);landmarks.add(landmark);
		landmark=new NeoLandmark("NOSE_BOTTOM_CENTER",2246,1831);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_TOP_BOUNDARY",3267,1131);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_RIGHT_CORNER",3153,1375);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_BOTTOM_BOUNDARY",3069,1147);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYE_LEFT_CORNER",3123,892);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_TOP_BOUNDARY",3371,2349);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_RIGHT_CORNER",3260,2589);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_BOTTOM_BOUNDARY",3179,2363);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYE_LEFT_CORNER",3233,2133);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EYEBROW_UPPER_MIDPOINT",3642,1119);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EYEBROW_UPPER_MIDPOINT",3734,2370);landmarks.add(landmark);
		landmark=new NeoLandmark("LEFT_EAR_TRAGION",2756,457);landmarks.add(landmark);
		landmark=new NeoLandmark("RIGHT_EAR_TRAGION",2917,3076);landmarks.add(landmark);
		landmark=new NeoLandmark("FOREHEAD_GLABELLA",3524,1772);landmarks.add(landmark);
		landmark=new NeoLandmark("CHIN_GNATHION",1170,1892);landmarks.add(landmark);
		landmark=new NeoLandmark("CHIN_LEFT_GONION",1780,854);landmarks.add(landmark);
		landmark=new NeoLandmark("CHIN_RIGHT_GONION",1929,2783);landmarks.add(landmark);
		landmark=new NeoLandmark("UNRECOGNIZED",2293,972);landmarks.add(landmark);
		landmark=new NeoLandmark("UNRECOGNIZED",2430,2587);landmarks.add(landmark);
				
		pose = new NeoPose(2.8008766f, 86.328804f, -3.8977976f);
		
		return this;
	}
	
	
}
