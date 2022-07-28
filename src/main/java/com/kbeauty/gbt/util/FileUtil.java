package com.kbeauty.gbt.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import com.kbeauty.gbt.entity.enums.StoragePath;


public class FileUtil {
	public final static String sep = "/";
	
	public static String getStoragePath(String type, String userId, String fileName, StoragePath storagePath) {
		//대분류/사용자/일자/파일
		//대분류/사용자/파일
		StringBuffer sb = new StringBuffer();
		sb.append(type).append(sep);
		
		switch (storagePath) {
		case USER:
			sb.append(fileName);
			break;
		
		case CONTENT_USER: // 사용중
		case AI_USER:
			sb.append(userId).append(sep);			
			sb.append(fileName);
			break;
		
		case CONTENT_USER_DAILY: 
		case AI_USER_DAILY: // 사용중
			sb.append(userId).append(sep);
			sb.append(CommonUtil.getDate()).append(sep);
			sb.append(fileName);
			break;
		case TRAINING : // 사용중
			sb.append(userId).append(sep);
			sb.append(CommonUtil.getDate()).append(sep);
			sb.append(fileName);
			break;
		default:
			sb.append(fileName);			
			break;
		}
		
		return sb.toString();
	}
	
	public static String getNeoFileName(String uuid, String fileName) {
		StringBuffer sb = new StringBuffer();
		sb.append(uuid).append(".").append(getExtension(fileName));
		return sb.toString();
	}
	
	public static String getExtension(String fileName) {
		String extention = FilenameUtils.getExtension(fileName).toLowerCase();
		
		if("jpeg".equals(extention)) {
			extention = "jpg";
		}
		
		return extention;
	}
	
	public static String getExtensionByContentType(String contentType) {		
		String[] arr = contentType.toLowerCase().split(sep);
		String extention = null;
		if(arr.length == 1) {
			extention = arr[0];
		}else {
			extention = arr[1];
		}
		
		if("jpeg".equals(extention)) {
			extention = "jpg";
		}
		
		return extention;
	}
	
	public static byte[] getBytesByContentType(BufferedImage bi, String contentType) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, getExtensionByContentType(contentType), baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
	
	public static byte[] getBytes(BufferedImage bi, String extension) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, extension, baos);
		byte[] bytes = baos.toByteArray();
		return bytes;
	}
	
	public static ByteArrayResource getByteArrayResource(BufferedImage bi, String fileName) throws Exception{
		String extension = getExtension(fileName);
		byte[] imageBytes = getBytes(bi, extension);
		
		ByteArrayResource fileResource = new ByteArrayResource(imageBytes) {
            // 기존 ByteArrayResource의 getFilename 메서드 override 
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        
        return fileResource;
	}
	
	public static String getGsUrl(String bucket, String storagePath) {
//		  gs://bucket-beautage-com/ai/bec8b2bc98b54fcdbfcbc8588ce3c26a/20201202/dfae7c18e230495ea68c790af333889d.jpg
			
		StringBuffer sb = new StringBuffer();
		sb.append("gs://").append(bucket).append(sep).append(storagePath);
		return sb.toString();
	}
	
	public static String getYoutubeId(String url) {
		// https://youtu.be/TQvgUW8Ydhs
		// https://www.youtube.com/embed/TQvgUW8Ydhs
		// https://www.youtube.com/watch?v=APjv7NdsYnU
		//https://youtu.be/APjv7NdsYnU
		
		String youtubeId = null;
		if(StringUtil.isEmpty(url)) return "";
		
		
		if(url.indexOf("watch") > -1 ) {
			int idx = url.indexOf("?v=");
			youtubeId = url.substring(idx+3);
			
		}else {			
			int idx = url.lastIndexOf("/");
			
			int queIdx = url.lastIndexOf("?");
			if(queIdx > -1) {			
				youtubeId = url.substring(idx+1, queIdx);
			}else {
				youtubeId = url.substring(idx+1);
			}
		}
			
		return youtubeId;
	}
	
	public static boolean isEmpty(MultipartFile file) {
		if(file == null) return true;
		try {
			int len = file.getBytes().length;
			return len == 0;
		} catch (IOException e) {
			return true;
		}
	}


	
	public static void main(String[] args) {
		String url = "https://www.youtube.com/watch?v=APjv7NdsYnU";
		System.out.println(FileUtil.getYoutubeId(url));
	}
	
	
}
