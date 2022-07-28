package com.kbeauty.gbt.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.BoundingPoly;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation.Landmark;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import java.util.Map.Entry;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;

public class VisionUtil {
	
	
	public boolean detectFaces(BufferedImage faceImage, String extention) throws Exception {
		boolean result = false;
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(faceImage, extention, os);
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		ByteString imgBytes = ByteString.readFrom(is);
				
		BufferedImage cropImg = null;
		int[] wh = null;		
		List<AnnotateImageRequest> requests = new ArrayList<>();
		BufferedImage a = null;		

		Image img = Image.newBuilder().setContent(imgBytes).build();
		Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
			BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
			List<AnnotateImageResponse> responses = response.getResponsesList();

			for (AnnotateImageResponse res : responses) {
				if (res.hasError()) {
					System.out.format("Error: %s%n", res.getError().getMessage());
					return result;
				}
				
				int i = 0;
				int demension = 0;
				int tempDemension = 0;
				for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
					i++;
//					System.out.println("===================================");
//					BoundingPoly fdBoundingPoly = annotation.getBoundingPoly();
					BoundingPoly fdBoundingPoly = annotation.getFdBoundingPoly();

/////////////////////////////////////////////////////////////////////////////////////////////					
//LandMark 관련 					
					List<Landmark> landmarksList = annotation.getLandmarksList();
					for (Landmark landmark : landmarksList) {
						Map<FieldDescriptor, Object> allFields = landmark.getAllFields();
						Set<Entry<FieldDescriptor, Object>> entrySet = allFields.entrySet();
						for (Entry<FieldDescriptor, Object> entry : entrySet) {
							entry.getKey();
							entry.getValue();
						}
					}
					
/////////////////////////////////////////////////////////////////////////////////////////////					
//					System.out.println(fdBoundingPoly);
					// 이전 데이터와 사이브를 비교애서 작으면 처리 안함. ==> 향후에는 DB로 입력해서 재처리되게해야 함.					
					wh = convertWH(fdBoundingPoly);
					demension = getDemension(fdBoundingPoly);
					if( tempDemension > demension ) continue;
					tempDemension = demension;
					
					cropImg = Scalr.crop(faceImage, wh[0], wh[1], wh[2], wh[3]);
					result = true;					
				}
			}
		}
		
		return result;
	}
	
	private int[] convertWH(BoundingPoly fdBoundingPoly) {
		int[] result = new int[4];
		
		int x = fdBoundingPoly.getVertices(0).getX();
		int y = fdBoundingPoly.getVertices(0).getY();
		
		int w = fdBoundingPoly.getVertices(2).getX() - x;
		int h = fdBoundingPoly.getVertices(2).getY() - y;
		
		result[0] = x;
		result[1] = y;
		result[2] = w;
		result[3] = h;
		
		return result;
	}
	
	
	private int getDemension(BoundingPoly fdBoundingPoly) {
				
		int x = fdBoundingPoly.getVertices(0).getX();
		int y = fdBoundingPoly.getVertices(0).getY();
		
		int w = fdBoundingPoly.getVertices(2).getX() - x;
		int h = fdBoundingPoly.getVertices(2).getY() - y;
				
		return w * h;
	}


}
