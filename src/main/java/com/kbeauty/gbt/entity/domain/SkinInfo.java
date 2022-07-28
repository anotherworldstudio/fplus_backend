package com.kbeauty.gbt.entity.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.kbeauty.gbt.ai.Dimension;
import com.kbeauty.gbt.ai.NeoLandmark;
import com.kbeauty.gbt.ai.NeoPose;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "SKININFO")
@Data
@EqualsAndHashCode(callSuper = false)
public class SkinInfo extends CommonDomain {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long seq                ;	
	@Column(name = "skinid")	    private String skinId;
	@Column(name = "infotype")	    private String infoType; /** 1000:얼굴정보/2000:landmark/3000:기타정보 **/
	@Column(name = "itemcode")	    private String itemCode;
	@Column(name = "xpos") 	private int xPos;
	@Column(name = "ypos") 	private int yPos;
	private int width;
	private int height;	
	private double pitch;
	private double roll;
	private double yaw;
	
	public SkinInfo() {
		
	}
	
	public SkinInfo(Dimension dimension, String imgId) {
		this();
		setSkinId(imgId);
		setItemCode("11000");
		setInfoType("1000");
		setXPos(dimension.getX());
		setYPos(dimension.getY());
		setWidth(dimension.getWidth());
		setHeight(dimension.getHeight());
	}
	
	public SkinInfo(NeoLandmark landmark, String imgId) {
		this();
		setSkinId(imgId);
		setItemCode(landmark.getType());
		setInfoType("2000");
		setXPos(landmark.getX());
		setYPos(landmark.getY());		
	}
	
	public SkinInfo(NeoPose pose, String imgId) {
		this();
		setSkinId(imgId);
		setItemCode("11010");
		setInfoType("3000");
		setPitch(pose.getPitch());
		setRoll(pose.getRoll());
		setYaw(pose.getYaw());
	}
}
