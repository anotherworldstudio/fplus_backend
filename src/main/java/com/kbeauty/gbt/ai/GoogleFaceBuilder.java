package com.kbeauty.gbt.ai;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation.Landmark;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.kbeauty.gbt.entity.CommonConstants;
import com.kbeauty.gbt.entity.enums.Orientation;
import com.kbeauty.gbt.util.CommonUtil;
import com.kbeauty.gbt.util.CroppingUtil;
import com.kbeauty.gbt.util.ImageUtil;

public class GoogleFaceBuilder {
	
	public static void main(String[] args) throws Exception {
		
//		int i = 0;
//		
//		for( ; i < 10000;i++ ) {			
//			String guid = CommonUtil.getGuid();
//			System.out.println(guid);,
//		}
//		
//		if( i > 0) return;
		
		VisionFace visionFace = new VisionFace();
		GoogleFaceBuilder builder = new GoogleFaceBuilder();		
		String inputDir = "E:\\DEV\\img_crop\\example\\";
		
		String abnor = "org_small_abnor.jpg"; //우측
		String nor = "org_small_nor.jpg";
		
		abnor = nor;//정상
		
		abnor = "crop_test.jpg";
		
//		abnor = "org_small_abnor2.jpg"; //좌측
		
//		abnor = "org_small_abnor3.jpg"; //180

		Path inputPath = null;
		Path outputPath = null;
		Path outputPath2 = null;
		Path outputPath3 = null;
		Path outputPath4 = null;
		
//		inputPath = Paths.get(inputDir+nor);
//		outputPath = Paths.get(inputDir+"\\output\\out_"+nor);
//		face = face.getTempNormalData(); //Mock 정상 파일 
				
		
		inputPath = Paths.get(inputDir+abnor);
		outputPath = Paths.get(inputDir+"\\output\\out_"+abnor);
		outputPath2 = Paths.get(inputDir+"\\output\\out_2"+abnor);
		outputPath3 = Paths.get(inputDir+"\\output\\treaty_"+abnor);
		
		BufferedImage img = ImageIO.read(inputPath.toFile());	    
		
//		face = face.getTempAbNormalData(); //Mock 누운 파일
		visionFace = builder.getFace(inputPath.toFile()); //실제 구글 vision 사용
		
		visionFace.setWidth(img.getWidth());
		visionFace.setHeight(img.getHeight());				
		System.out.println(visionFace);	
				
		Orientation orientation = visionFace.genOrientation(); //기울기 조정
		System.out.println("===========================");
		
		System.out.println(visionFace);		
		BufferedImage img2 = null;
		
	    img2 = ImageUtil.rotate(img, orientation, false);
//	    builder.annotateWithFace(img2, visionFace); // landmark 표시 
	    
	    //진단 이미지 cropping 
	    BufferedImage treatyImg = CroppingUtil.cropNResize(img2, visionFace, CommonConstants.TREATY_SIZE);
	    
	    visionFace.adjustDemension(treatyImg.getWidth(), treatyImg.getHeight()); //진단 이미지에 맞춰서 landmark 조정 
//	    builder.annotateWithFace(treatyImg, visionFace); // landmark 표시
	    //
		int width =  treatyImg.getWidth();
		int height = treatyImg.getHeight();		
		ImgCrop imgCrop = new ImgCrop(width, height);		
		imgCrop.setChild();
		
		BufferedImage tZoneTopImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.TZONE_TOP);
		BufferedImage tZoneBtmImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.TZONE_BOTTOM);
		BufferedImage uZoneLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.UZONE_LEFT);
		BufferedImage uZoneRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.UZONE_RIGHT);
		
		
		BufferedImage forheadLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.FORHEAD_LEFT);
		BufferedImage forheadRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.FORHEAD_RIGHT);
		
		BufferedImage forheadMidImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.FORHEAD_MID);
		
		BufferedImage glabellaImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.GLABELLA);
		BufferedImage eyeLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.EYE_LEFT);
		BufferedImage eyeRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.EYE_RIGHT);
		
		BufferedImage cheekLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.CHEEK_LEFT);
		BufferedImage cheekRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.CHEEK_RIGHT);
		
		BufferedImage mouthImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.MOUTH);
		
		BufferedImage mouthLeftImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.MOUTH_LEFT);
		BufferedImage mouthRightImg = CroppingUtil.crop(visionFace, treatyImg, imgCrop, FaceEnum.MOUTH_RIGHT);
		
		
		BufferedImage aiImg = ImageUtil.resize(treatyImg, CommonConstants.TREATY_SIZE);
		
		saveCropFile(inputDir, abnor, tZoneTopImg, FaceEnum.TZONE_TOP);		
		saveCropFile(inputDir, abnor, tZoneBtmImg, FaceEnum.TZONE_BOTTOM);		
		saveCropFile(inputDir, abnor, uZoneLeftImg, FaceEnum.UZONE_LEFT);		
		saveCropFile(inputDir, abnor, uZoneRightImg, FaceEnum.UZONE_RIGHT);
		
		saveCropFile(inputDir, abnor, forheadLeftImg, FaceEnum.FORHEAD_LEFT);
		saveCropFile(inputDir, abnor, forheadRightImg, FaceEnum.FORHEAD_RIGHT);
		saveCropFile(inputDir, abnor, forheadMidImg, FaceEnum.FORHEAD_MID);
		
		saveCropFile(inputDir, abnor, glabellaImg, FaceEnum.GLABELLA);
		
		saveCropFile(inputDir, abnor, eyeLeftImg, FaceEnum.EYE_LEFT);
		saveCropFile(inputDir, abnor, eyeRightImg, FaceEnum.EYE_RIGHT);
		
		saveCropFile(inputDir, abnor, cheekLeftImg, FaceEnum.CHEEK_LEFT);
		saveCropFile(inputDir, abnor, cheekRightImg, FaceEnum.CHEEK_RIGHT);
		
		saveCropFile(inputDir, abnor, mouthImg, FaceEnum.MOUTH);
		saveCropFile(inputDir, abnor, mouthLeftImg, FaceEnum.MOUTH_LEFT);
		saveCropFile(inputDir, abnor, mouthRightImg, FaceEnum.MOUTH_RIGHT);
		
		
//		outputPath4 = Paths.get(inputDir+"\\output\\tb_"+abnor);		
//		ImageIO.write(tZoneBtmImg, "jpg", outputPath4.toFile());
//		
//		outputPath4 = Paths.get(inputDir+"\\output\\ul_"+abnor);		
//		ImageIO.write(uZoneLeftImg, "jpg", outputPath4.toFile());
//		
//		outputPath4 = Paths.get(inputDir+"\\output\\ur_"+abnor);		
//		ImageIO.write(uZoneRightImg, "jpg", outputPath4.toFile());
		
		outputPath4 = Paths.get(inputDir+"\\output\\ai_"+abnor);		
		ImageIO.write(aiImg, "jpg", outputPath4.toFile());
		
	    
		//builder.annotateWithFace(img, face);		
		
//		NeoPose pose = face.getPose();
//		float roll = pose.getRoll();
//		
//		List<NeoLandmark> landmarks = face.getLandmarks();
//		Dimension dimension = face.getDimension();
//		if(roll > 150) { // 뒤집힌 경우
//			img2 = ImageUtil.rotate(img, Orientation.R180);			
//			for (NeoLandmark neoLandmark : landmarks) {
//				Point p = new Point(img.getWidth() - neoLandmark.getX(), img.getHeight() - neoLandmark.getY());
//				neoLandmark.setPoint(p);
//			}
//	   }else if(roll > 50) { //우측 
//			// 회전정보, 1. 0도, 3. 180도, 6. 270도, 8. 90도 회전한 정보
//			img2 = ImageUtil.rotate(img, Orientation.R090);
//			
//			for (NeoLandmark neoLandmark : landmarks) {
//				Point p = new Point(neoLandmark.getY(), img.getWidth() - neoLandmark.getX());
//				neoLandmark.setPoint(p);
//			}		
//		}else if(roll < -50) { //좌측 
//			img2 = ImageUtil.rotate(img, Orientation.R270);
//			
//			for (NeoLandmark neoLandmark : landmarks) {
//				Point p = new Point(img.getHeight() - neoLandmark.getY(), neoLandmark.getX());
//				neoLandmark.setPoint(p);
//			}
//		}else if(roll < -150) { //뒤집힌 경우 
//			img2 = ImageUtil.rotate(img, Orientation.R180);
//			
//			for (NeoLandmark neoLandmark : landmarks) {
//				Point p = new Point(img.getWidth() - neoLandmark.getX(), img.getHeight() - neoLandmark.getY());
//				neoLandmark.setPoint(p);
//			}
//		}else {//정상인 경우 
//			img2 = img;
//		}
		
		
		
		
//		ImageIO.write(img, "jpg", outputPath.toFile());
//		
//		ImageIO.write(img2, "jpg", outputPath2.toFile());
//		
//		ImageIO.write(treatyImg, "jpg", outputPath3.toFile());
	}

	public static void saveCropFile(String inputDir, String fileName, BufferedImage cropImg, FaceEnum faceEnum) throws IOException {
		String cropImgName = CroppingUtil.getCropFileName(fileName, faceEnum);
		Path outputPath4 = Paths.get(inputDir+"\\output\\"+ cropImgName);				
		ImageIO.write(cropImg, "jpg", outputPath4.toFile());
	}
	
	private void annotateWithFace(BufferedImage img, VisionFace face) {
		Graphics2D gfx = img.createGraphics();
		
		Polygon poly = new Polygon();
		for (Point point : face.getDimension().getPointList()) {			
			poly.addPoint(point.getX(), point.getY());
		}
		
		
		gfx.setStroke(new BasicStroke(10));
		gfx.setColor(new Color(0x00ff00));
		gfx.draw(poly);
		
		gfx.setPaint(Color.RED);
		List<NeoLandmark> landmarks = face.getLandmarks();
		
		int x;
		int y;
		for (NeoLandmark neoLandmark : landmarks) {
			if( true || neoLandmark.getType().equals("RIGHT_EYE") || neoLandmark.getType().equals("LEFT_EYE") ) {
				x = neoLandmark.getPoint().getX();
				y = neoLandmark.getPoint().getY();
				
				gfx.drawRect(x, y, 40, 40);
			}
			//gfx.drawString(neoLandmark.getType(), x, y);
			//gfx.setFont(new Font("TimesRoman", Font.PLAIN, 10));
			
		}
		
//		poly = new Polygon();
//		NeoLandmark neoLandmark;		
//		neoLandmark = face.getLandmark("LEFT_EYE_BOTTOM_BOUNDARY");poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
////		neoLandmark = face.getLandmark("LEFT_EYE_RIGHT_CORNER"   );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
////		neoLandmark = face.getLandmark("MIDPOINT_BETWEEN_EYES"   );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
////		neoLandmark = face.getLandmark("NOSE_TIP"                );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("NOSE_BOTTOM_LEFT"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("MOUTH_LEFT"              );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("CHIN_LEFT_GONION"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("LEFT_EAR_TRAGION"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		
//		gfx.setStroke(new BasicStroke(2));
//		gfx.setColor(Color.RED);
//		gfx.draw(poly);
//		
//		poly = new Polygon();
//		neoLandmark = face.getLandmark("MIDPOINT_BETWEEN_EYES"   );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("NOSE_BOTTOM_LEFT"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("NOSE_BOTTOM_CENTER"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("NOSE_BOTTOM_RIGHT"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		
//		gfx.setStroke(new BasicStroke(2));
//		gfx.setColor(Color.CYAN);
//		gfx.draw(poly);
//		
//		poly = new Polygon();
//		neoLandmark = face.getLandmark("MIDPOINT_BETWEEN_EYES"   );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("RIGHT_OF_LEFT_EYEBROW"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("FOREHEAD_GLABELLA"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		neoLandmark = face.getLandmark("LEFT_OF_RIGHT_EYEBROW"        );poly.addPoint(neoLandmark.getX(), neoLandmark.getY());
//		
//		gfx.setStroke(new BasicStroke(3));
//
//		gfx.setColor(Color.ORANGE);
//		gfx.draw(poly);
		
	}
	
	public VisionFace getFace(BufferedImage orgFileBuffer, String fileExt)  throws Exception {	
		int width = orgFileBuffer.getWidth();
		int height = orgFileBuffer.getHeight();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( orgFileBuffer, fileExt, baos );
		baos.flush();		
		InputStream fileInputStream = new ByteArrayInputStream(baos.toByteArray());		
		baos.close();
		VisionFace face = getFace(fileInputStream);
		face.setWidth(width);
		face.setHeight(height);
		return face;
	}
	
	public VisionFace getFace(File orgFile)  throws Exception {
		FileInputStream fileInputStream = new FileInputStream(orgFile);
		return getFace(fileInputStream);
	}

	private VisionFace getFace(InputStream fileInputStream) throws Exception {
		FaceAnnotation annotation = getFaceAnnotationByInputStream(fileInputStream);		
		if(annotation == null) return null; //ERROR 처리 해야 함.		
		VisionFace face = new VisionFace();
		setFaceInfo(face, annotation);		
		return face;
	}
	
	public VisionFace getFace(String gsUrl) throws Exception {
		FaceAnnotation annotation = getFaceAnnotationByStorageId(gsUrl);		
		if(annotation == null) return null; //ERROR 처리 해야 함.
		VisionFace face = new VisionFace();
		setFaceInfo(face, annotation);		
		return face;
	}

	private void setFaceInfo(VisionFace face, FaceAnnotation annotation) {
		BoundingPoly fdBoundingPoly = annotation.getFdBoundingPoly();		
		Dimension dimension = getDimension(fdBoundingPoly);		
		NeoPose pose = getPose(annotation);
		List<NeoLandmark> landmarks = new ArrayList<>();
		
		/////////////////////////////////////////////////////////////////////////////////////////////
		// LandMark 관련
		NeoLandmark neoLandmark = null; 
		List<Landmark> landmarksList = annotation.getLandmarksList();
		for (Landmark landmark : landmarksList) {			
			neoLandmark = getLandmark(landmark);
			landmarks.add(neoLandmark);
			
//			landmark.getType().toString();
//			
//			landmark.getPosition();
//			
//			Map<FieldDescriptor, Object> allFields = landmark.getAllFields();
//			Set<Entry<FieldDescriptor, Object>> entrySet = allFields.entrySet();
//			for (Entry<FieldDescriptor, Object> entry : entrySet) {
//				System.out.print(entry.getKey() + " : ");
//				System.out.println(entry.getValue());
//				
//			}
		}
		
		face.setDimension(dimension);
		face.setLandmarks(landmarks);
		face.setPose(pose);
	}
	
	private FaceAnnotation getFaceAnnotationByInputStream(InputStream inputStream) throws Exception {
		 
		ByteString imgBytes = ByteString.readFrom(inputStream);
		Image img = Image.newBuilder().setContent(imgBytes).build();

// 향후에 google storage 사용하면 아래와 같이 url로 변경 처리		
		
//		String inputImageUri = "gs://cloud-samples-data/vision/label/wakeupcat.jpg";
//		String outputUri = "gs://YOUR_BUCKET_ID/path/to/save/results/";
//		    
//		ImageSource source = ImageSource.newBuilder().setImageUri(inputImageUri).build();
//	    Image image = Image.newBuilder().setSource(source).build();
		
		return getFaceAnnotation(img);
	}
	
	private FaceAnnotation getFaceAnnotationByStorageId(String gsUrl) throws Exception {
		
//		String gsUrl = "gs://cloud-samples-data/vision/label/wakeupcat.jpg";
		    
		ImageSource source = ImageSource.newBuilder().setImageUri(gsUrl).build();
	    Image img = Image.newBuilder().setSource(source).build();
		
		return getFaceAnnotation(img);
	}

	private FaceAnnotation getFaceAnnotation(Image img) throws IOException {
		FaceAnnotation result = null;		
		List<AnnotateImageRequest> requests = new ArrayList<>();		
		Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					System.out.format("Error: %s%n", res.getError().getMessage());
					return null;
				}

				
				int demension = 0;
				int tempDemension = 0;				
				for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {				
					BoundingPoly fdBoundingPoly = annotation.getFdBoundingPoly();
					
					demension = getDemension(fdBoundingPoly);
					if( tempDemension > demension ) {
						continue;
					}else {
						tempDemension = demension;
						result = annotation;
					}
					
					
					System.out.println("목돌리기" + " : " + annotation.getPanAngle());
					System.out.println("얼굴돌리기" + " : " + annotation.getRollAngle());
					System.out.println("고개숙이기" + " : " + annotation.getTiltAngle());
					
					/////////////////////////////////////////////////////////////////////////////////////////////
					// LandMark 관련
					List<Landmark> landmarksList = annotation.getLandmarksList();
					for (Landmark landmark : landmarksList) {
						landmark.getType().toString();
						Map<FieldDescriptor, Object> allFields = landmark.getAllFields();
						Set<Entry<FieldDescriptor, Object>> entrySet = allFields.entrySet();
						for (Entry<FieldDescriptor, Object> entry : entrySet) {
							System.out.print(entry.getKey() + " : ");
							System.out.println(entry.getValue());
							
						}
					}					
				}
			}
		}

		return result;
	}
	
	private int getDemension(BoundingPoly fdBoundingPoly) {		
		int x = fdBoundingPoly.getVertices(0).getX();
		int y = fdBoundingPoly.getVertices(0).getY();
		
		int w = fdBoundingPoly.getVertices(2).getX() - x;
		int h = fdBoundingPoly.getVertices(2).getY() - y;
				
		return w * h;
	}
	
	private Dimension getDimension(BoundingPoly fdBoundingPoly) {
		int x = fdBoundingPoly.getVertices(0).getX();
		int y = fdBoundingPoly.getVertices(0).getY();
		
		int width = fdBoundingPoly.getVertices(2).getX() - x;
		int height = fdBoundingPoly.getVertices(2).getY() - y;
		
		return new Dimension(x, y, width, height);
	}
	
	private NeoPose getPose(FaceAnnotation annotation) {
		return new NeoPose(annotation.getPanAngle(), annotation.getRollAngle(), annotation.getTiltAngle());
	}
	
	private NeoLandmark getLandmark(Landmark landmark) {
		return new NeoLandmark(landmark.getType().toString(), new Point(landmark.getPosition().getX(), landmark.getPosition().getY()));		
	}
	
}
