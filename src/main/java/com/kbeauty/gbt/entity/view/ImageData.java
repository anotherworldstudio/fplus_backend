package com.kbeauty.gbt.entity.view;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;

import lombok.Data;

@Data
public class ImageData {
	
	String imageUrl;
	int width;
	int height;
	String extension;

	public void setWidthHeight() {
		if (imageUrl != null && !"".equals(imageUrl)) {
			try {
				URL url = new URL(imageUrl);
				Image image  = ImageIO.read(url);
				width = image.getWidth(null);
				height = image.getHeight(null);
				setExtension();
			} catch (Exception e) {
				System.out.println("이미지 파일이 아닙니다.");
			}
		} 
	}
	public void setExtension() {
		String[] array = imageUrl.split("[.]");
		extension = array[array.length-1];
	}

}
