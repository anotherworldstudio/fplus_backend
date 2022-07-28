package com.kbeauty.gbt.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.kbeauty.gbt.ai.Dimension;
import com.kbeauty.gbt.ai.FaceEnum;
import com.kbeauty.gbt.ai.ImgCrop;
import com.kbeauty.gbt.ai.Point;
import com.kbeauty.gbt.ai.VisionFace;
import com.kbeauty.gbt.entity.CommonConstants;

public class CroppingUtil {
	
	public static BufferedImage crop(BufferedImage orgImg, VisionFace face) {
		Dimension dimension = face.getDimension();
		BufferedImage cropImg = Scalr.crop(orgImg, dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
		return cropImg;
	}
	
	public static BufferedImage cropNResize(BufferedImage orgImg, VisionFace face, int size) throws Exception{
		Dimension dimension = face.getDimension();
		BufferedImage cropImg = Scalr.crop(orgImg, dimension.getX(), dimension.getY(), dimension.getWidth(), dimension.getHeight());
		BufferedImage aiImg = ImageUtil.resize(cropImg, size);
		return aiImg;
	}
	
	public static BufferedImage crop(VisionFace face, BufferedImage img768, ImgCrop imgCrop, FaceEnum faceEnum) throws IOException {		
		ImgCrop childCrop = null;		
		Point p = null;
		Point p2 = null;
		switch (faceEnum) {
			case FORHEAD_LEFT: //fl
				p = face.getLandmark("LEFT_EYEBROW_UPPER_MIDPOINT").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case FORHEAD_MID: //fm 미사용 
				p = face.getLandmark("FOREHEAD_GLABELLA").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case FORHEAD_RIGHT: //fr
				p = face.getLandmark("RIGHT_EYEBROW_UPPER_MIDPOINT").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case GLABELLA: //gl
				p = face.getLandmark("FOREHEAD_GLABELLA").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.CT);
				break;
				
			case EYE_LEFT: //el
				p2 = face.getLandmark("NOSE_BOTTOM_LEFT").getPoint();
				p = face.getLandmark("LEFT_EYE").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TP);
				break;
				
			case EYE_RIGHT: //er
				p2 = face.getLandmark("NOSE_BOTTOM_RIGHT").getPoint();
				p = face.getLandmark("RIGHT_EYE").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TP);
				break;
				
			case CHEEK_LEFT: //cl
//				p = face.getLandmark("LEFT_EYE").getPoint();
////				p2 = face.getLandmark("NOSE_TIP").getPoint();
//				p2 = face.getLandmark("NOSE_BOTTOM_CENTER").getPoint();
////				childCrop = imgCrop.crop(p, p2, ImgCrop.TL);
//				childCrop = imgCrop.crop(p, p2, ImgCrop.TP);
				
				p = face.getLandmark("NOSE_BOTTOM_CENTER").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.RC);
				break;
				
			case CHEEK_RIGHT: //cr
//				p = face.getLandmark("RIGHT_EYE").getPoint();
////				p2 = face.getLandmark("NOSE_TIP").getPoint();
//				p2 = face.getLandmark("NOSE_BOTTOM_CENTER").getPoint();				
////				childCrop = imgCrop.crop(p, p2, ImgCrop.TR);
//				childCrop = imgCrop.crop(p, p2, ImgCrop.TP);
			
				p = face.getLandmark("NOSE_BOTTOM_CENTER").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.LC);
				break;
				
			case MOUTH:			// mo	
				p = face.getLandmark("CHIN_GNATHION").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
			
			case MOUTH_LEFT:			// ml
				p = face.getLandmark("NOSE_BOTTOM_CENTER").getPoint();				
				childCrop = imgCrop.crop(p, ImgCrop.TL);				
				break;
				
			case MOUTH_RIGHT:			// mr
				p = face.getLandmark("NOSE_BOTTOM_CENTER").getPoint();				
				childCrop = imgCrop.crop(p, ImgCrop.TR);				
				break;
				
			case TZONE_TOP: //tt
				p = face.getLandmark("FOREHEAD_GLABELLA").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.CT);
				break;
				
			case TZONE_BOTTOM: //tb
				p = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.CT);
				break;

			case UZONE_LEFT: //ul
				p = face.getLandmark("LEFT_EYE_BOTTOM_BOUNDARY").getPoint();
				p2 = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TL);
				break;
				
			case UZONE_RIGHT: //ur 
				p = face.getLandmark("RIGHT_EYE_BOTTOM_BOUNDARY").getPoint();
				p2 = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TR);
				break;
				

			default:
				break;
		}  
		
		BufferedImage cropImg = Scalr.crop(img768, childCrop.getX(), childCrop.getY(), childCrop.getWidth(), childCrop.getHeight());
		return cropImg;
	}
	
	public static String getCropFileName(String imgName, FaceEnum faceEnum) throws IOException {		
		StringBuffer sb = new StringBuffer();
		
		switch (faceEnum) {
			case FORHEAD_LEFT: //fl
				sb.append("FL").append("_").append(imgName);				
				break;
				
			case FORHEAD_MID: //fm 미사용 
				sb.append("FM").append("_").append(imgName);
				break;
				
			case FORHEAD_RIGHT: //fr
				sb.append("FR").append("_").append(imgName);
				break;
				
			case GLABELLA: //gl
				sb.append("GL").append("_").append(imgName);
				break;
				
			case EYE_LEFT: //el
				sb.append("EL").append("_").append(imgName);
				break;
				
			case EYE_RIGHT: //er
				sb.append("ER").append("_").append(imgName);
				break;
				
			case CHEEK_LEFT: //cl
				sb.append("CL").append("_").append(imgName);
				break;
				
			case CHEEK_RIGHT: //cr
				sb.append("CR").append("_").append(imgName);
				break;
				
			case MOUTH:			// mo	
				sb.append("MO").append("_").append(imgName);
				break;
				
			case MOUTH_LEFT:			// ml
				sb.append("ML").append("_").append(imgName);
				break;
				
			case MOUTH_RIGHT:			// mr
				sb.append("MR").append("_").append(imgName);				
				break;				
			case TZONE_TOP: //tt
				sb.append("TT").append("_").append(imgName);
				break;
				
			case TZONE_BOTTOM: //tb
				sb.append("TB").append("_").append(imgName);
				break;

			case UZONE_LEFT: //ul
				sb.append("UL").append("_").append(imgName);
				break;
				
			case UZONE_RIGHT: //ur 
				sb.append("UR").append("_").append(imgName);
				break;
				

			default:
				break;
		}
		
		
		return sb.toString();
	}

}
