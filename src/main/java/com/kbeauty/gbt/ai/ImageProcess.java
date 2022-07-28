package com.kbeauty.gbt.ai;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

public class ImageProcess {
	
	public static final String BASE_OUTPUT = "E:\\beautage\\poc\\facelandmaker\\cropping\\cropping\\";
//	public static final String BASE_OUTPUT = "E:\\beautage\\poc\\facelandmaker\\cropping\\";

	
	public static final String BASE_DIR = "E:\\beautage\\poc\\facelandmaker\\cropping\\treat\\";
	
	public static void main(String[] args) throws Exception {
//		String filePath = "E:\\beautage\\poc\\facelandmaker\\test01.jpg";
//		//filePath = "E:\\beautage\\poc\\facelandmaker\\org_file06.jpg";
//		Path inputPath = Paths.get(filePath);		
//		BufferedImage img = ImageIO.read(inputPath.toFile());
//		Path outputPath = Paths.get("E:\\beautage\\poc\\facelandmaker\\test_out.jpg");
//		ImageIO.write(img, "jpg", outputPath.toFile());
//		
		
		ImageProcess process = new ImageProcess();
		
		File dir = new File(BASE_DIR);
		if (!dir.isDirectory())
			return;
		
		String[] list = dir.list();		
		for (String dirs : list) {
			if(dirs.lastIndexOf("_F.jpeg") > 0 ||  dirs.lastIndexOf("_F.jpg") > 0) {
				process.process768(BASE_DIR, dirs);
			}	
//			System.out.println(dirs);
//			System.out.println(dirs.substring(2));
		}
		
	}
	
	/**
	 * 진단 파일 전용 Cropping
	 * @param dir
	 * @param fileName
	 * @throws Exception
	 */
	public void process768(String dir, String fileName) throws Exception{
		String ext = FilenameUtils.getExtension(fileName).toLowerCase();
		String baseName = FilenameUtils.getBaseName(fileName);
		
		// 원본 파일 얻기
		File orgFile = new File(dir + File.separator + fileName);
		
		// Face 얻어 오기
		GoogleFaceBuilder builder = new GoogleFaceBuilder();		
		VisionFace face = builder.getFace(orgFile); // 여기가 전체 사이즈로 위치 정보 가지고 옮.
		if(face == null) return; // 인식된 얼굴이 없음
		
//		Face face = new Face();
//		face =face.getTempData();
		
		face.setDir(BASE_OUTPUT);
		face.setBaseName(baseName);
		face.setExt(ext);
		
		BufferedImage img768 = ImageIO.read(orgFile);
		
		int width =  img768.getWidth();
		int height = img768.getHeight();		
		ImgCrop imgCrop = new ImgCrop(width, height);		
		imgCrop.setChild();
		
		cropNSave(face, img768, imgCrop, FaceEnum.FORHEAD_LEFT);
		cropNSave(face, img768, imgCrop, FaceEnum.FORHEAD_MID);
		cropNSave(face, img768, imgCrop, FaceEnum.FORHEAD_RIGHT);
		
		cropNSave(face, img768, imgCrop, FaceEnum.EYE_LEFT);
		cropNSave(face, img768, imgCrop, FaceEnum.GLABELLA);
		cropNSave(face, img768, imgCrop, FaceEnum.EYE_RIGHT);
		
		cropNSave(face, img768, imgCrop, FaceEnum.CHEEK_LEFT);  
		cropNSave(face, img768, imgCrop, FaceEnum.MOUTH);
		cropNSave(face, img768, imgCrop, FaceEnum.CHEEK_RIGHT);
		
		cropNSave(face, img768, imgCrop, FaceEnum.TZONE_TOP);
		cropNSave(face, img768, imgCrop, FaceEnum.TZONE_BOTTOM);
		
		cropNSave(face, img768, imgCrop, FaceEnum.UZONE_LEFT);
		cropNSave(face, img768, imgCrop, FaceEnum.UZONE_RIGHT);

	}
	
	private void cropNSave(VisionFace face, BufferedImage img768, ImgCrop imgCrop, FaceEnum faceEnum) throws IOException {
		String cropFileName = null;
		ImgCrop childCrop = null;		
		Point p = null;
		Point p2 = null;
		switch (faceEnum) {
			case FORHEAD_LEFT:
				p = face.getLandmark("LEFT_EYEBROW_UPPER_MIDPOINT").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case FORHEAD_MID:
				p = face.getLandmark("FOREHEAD_GLABELLA").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case FORHEAD_RIGHT:
				p = face.getLandmark("RIGHT_EYEBROW_UPPER_MIDPOINT").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case GLABELLA:
				p = face.getLandmark("FOREHEAD_GLABELLA").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.CT);
				break;
				
			case EYE_LEFT:
				p2 = face.getLandmark("NOSE_BOTTOM_LEFT").getPoint();
				p = face.getLandmark("LEFT_EYE").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TP);
				break;
				
			case EYE_RIGHT:
				p2 = face.getLandmark("NOSE_BOTTOM_RIGHT").getPoint();
				p = face.getLandmark("RIGHT_EYE").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TP);
				break;
				
			case CHEEK_LEFT:
				p = face.getLandmark("LEFT_EYE").getPoint();
				p2 = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TL);
				break;
				
			case CHEEK_RIGHT:
				p = face.getLandmark("RIGHT_EYE").getPoint();
				p2 = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TR);
				break;
				
			case MOUTH:				
				p = face.getLandmark("CHIN_GNATHION").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.BS);
				break;
				
			case TZONE_TOP:
				p = face.getLandmark("FOREHEAD_GLABELLA").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.CT);
				break;
				
			case TZONE_BOTTOM:
				p = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, ImgCrop.CT);
				break;

			case UZONE_LEFT:
				p = face.getLandmark("LEFT_EYE_BOTTOM_BOUNDARY").getPoint();
				p2 = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TL);
				break;
				
			case UZONE_RIGHT:
				p = face.getLandmark("RIGHT_EYE_BOTTOM_BOUNDARY").getPoint();
				p2 = face.getLandmark("NOSE_TIP").getPoint();
				childCrop = imgCrop.crop(p, p2, ImgCrop.TR);
				break;
				

			default:
				break;
		}
		
		String dir = face.getDir(); 
		String ext = face.getExt();
		String baseName = face.getBaseName(); 
		cropFileName = getFileName(baseName, ext, faceEnum);
		BufferedImage cropImg = Scalr.crop(img768, childCrop.getX(), childCrop.getY(), childCrop.getWidth(), childCrop.getHeight());		 
		Path outputPath = Paths.get(dir + File.separator + cropFileName);
		ImageIO.write(cropImg, ext, outputPath.toFile());
	}

	private String getFileName(String baseName, String ext, FaceEnum faceEnum) {		
		StringBuffer sb = new StringBuffer();
//		sb.append(baseName).append("_").append(faceEnum.getFileFix()).append(".").append(ext);
		
		sb
		.append("T_")
		.append(baseName.substring(2))
		.append("_").append(faceEnum.getFileFix())		
//		.append("_").append(faceEnum.getHanFix())
		.append(".").append(ext);
		
		return sb.toString();
	}
	
	
	public void process(String dir, String fileName) throws Exception{
		String extention = FilenameUtils.getExtension(fileName).toLowerCase();
		
		// 원본 파일 얻기
		File orgFile = new File(dir + File.pathSeparator + fileName);
		
		// 원본파일 Rotate and 저장 neoFile 
		BufferedImage orientBufferFile = getOrientBufferFile(orgFile);
		
		// Face 얻어 오기
		GoogleFaceBuilder builder = new GoogleFaceBuilder();
		
		VisionFace face = builder.getFace(orientBufferFile, extention); // 여기가 전체 사이즈로 위치 정보 가지고 옮.
		if(face == null) return; // 인식된 얼굴이 없음 
		// face의 디멘션으로 cropping ==> 진단 사진 OK
		
		/**
		 * TO-DO
		 * 원본이미지에서 cropping 하기 
		 */
		
		
		// 진단 파일 생성
		// ImgCrop imgCrop = new ImgCrop(width, height);
		
		// AI 파일 생성
		
		// Crop파일 생성
//		BufferedImage cropImg = Scalr.crop(faceImage, wh[0], wh[1], wh[2], wh[3]);
//		BufferedImage destImg = Scalr.resize(srcImg, dw, dh);
	}
	
	
	private BufferedImage getOrientBufferFile(File file) throws Exception{

		// 원본 파일의 Orientation 정보를 읽는다.
		int orientation = 1; // 회전정보, 1. 0도, 3. 180도, 6. 270도, 8. 90도 회전한 정보
		Metadata metadata; // 이미지 메타 데이터 객체
		Directory directory; // 이미지의 Exif 데이터를 읽기 위한 객체
		try {
			metadata = ImageMetadataReader.readMetadata(file);
			directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			if (directory != null) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
			}

		} catch (Exception e) {
			orientation = 1;
		}
		
		BufferedImage srcImg = ImageIO.read(file);
		// 회전 시킨다.
		switch (orientation) {
		case 6:
			srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_90);
			break;
		case 3:
			srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_180);
			break;
		case 8:
			srcImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_270);			
			break;
		default:
			orientation = 1;
			break;
		}
		
		return srcImg;
	}

}
