package com.kbeauty.gbt.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.kbeauty.gbt.entity.enums.Orientation;
import com.kbeauty.gbt.entity.view.ImageData;

public class ImageUtil {
	
	
	
	public static BufferedImage resize(BufferedImage srcImg, int basePix) throws Exception{		
		double rate = 0d;
		
		// 원본 이미지의 너비와 높이 입니다.
		int ow = srcImg.getWidth();
		int oh = srcImg.getHeight();
		int dw = 0;
		int dh = 0;
		
		if (ow > oh) { // 넓이 가 클경우
			dw = basePix;
			rate = basePix / (double) ow;
			dh = (int) (rate * oh);
		} else { // 길이가 클경우
			dh = basePix;
			rate = basePix / (double) oh;
			dw = (int) (rate * ow);
		}
		
		BufferedImage destImg = Scalr.resize(srcImg, dw, dh);
		return destImg;
	}
	
	
	public static Orientation setOrient(InputStream inputStream) throws Exception {
		// 원본 파일의 Orientation 정보를 읽는다.
		int orientation = 1; // 회전정보, 1. 0도, 3. 180도, 6. 270도, 8. 90도 회전한 정보

		Metadata metadata; // 이미지 메타 데이터 객체
		Directory directory; // 이미지의 Exif 데이터를 읽기 위한 객체		
		try {			
			metadata = ImageMetadataReader.readMetadata(inputStream);			
			directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
//			JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
			if (directory != null) {
				orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION); // 회전정보
			}
			
			return Orientation.R000.getOrientation(orientation);
			
		} catch (Exception e) {
			return Orientation.R000;			
		} 
	}
	
	public static BufferedImage rotate(BufferedImage srcImg, Orientation orientation, boolean isResize) throws Exception{
		// 회전 시킨다.
		BufferedImage resizeImg = null;
		switch (orientation) {
		case R270: //270
			resizeImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_270);
			break;
		case R180:
			resizeImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_180);
			break;
		case R090: // 90
			resizeImg = Scalr.rotate(srcImg, Scalr.Rotation.CW_90);
			break;
		default:	
			if(isResize) {				
				int ow = srcImg.getWidth();
				int oh = srcImg.getHeight();
				resizeImg = Scalr.resize(srcImg, ow, oh);
			}else {
				resizeImg = srcImg;
			}
			break;
		}
		return resizeImg; 
	}
	

	public static BufferedImage createThumbnail(MultipartFile mfile, int thumbWidth, int thumbHeight) throws Exception{

		InputStream in = mfile.getInputStream();
		BufferedImage originalImage = ImageIO.read(in);
		BufferedImage thumbImage = Scalr.resize(originalImage, thumbWidth, thumbHeight);
		
		// 만약 자동 조절 기능을 사용하려면
		// 가로 기준일 때,
		// BufferedImage thumbImage = Scalr.resize(originalImage,
		// Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, thumbWidth);
		// 세로 기준일 때,
		// BufferedImage thumbImage = Scalr.resize(originalImage,
		// Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_HEIGHT, thumbHeight);

		in.close();

		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(thumbImage, "jpg", baos);
		byte[] bytes = baos.toByteArray();
		  
		return thumbImage;
	}
	
    private static final String IMAGE_PNG_FORMAT = "png";

	public File getThumbnail(File source, File thumbnail) throws IOException, JCodecException {		
		int frameNumber = 0;
		Picture picture = FrameGrab.getFrameFromFile(source, frameNumber);
		
		
		
//		NIOUtils.readFromChannel(source)
//		NIOUtils.readableChannel(source)
//		
//		NIOUtils.rea
//
//		
//		
//		MultipartFile part = null;
//		part.getInputStream();
//		
//		
//		FileInputStream fin = null;
//        FileChannel inputChannel = null;
//        WritableByteChannel outputChannel = null;
//        
//        
//        fin = new FileInputStream(part.getInputStream());
//        inputChannel = fin.getChannel();
		
		
		
		BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
		ImageIO.write(bufferedImage, IMAGE_PNG_FORMAT, thumbnail);
		
		return thumbnail;
	}
	
	
//	public static ImageData getImageData(BufferedImage aiImg) {
//		ImageData img = new ImageData();
//		img.setImgUrl(a);
//		img.setWidthHeight();
//		return img;
//	}
}
