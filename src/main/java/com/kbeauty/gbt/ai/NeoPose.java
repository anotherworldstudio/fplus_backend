package com.kbeauty.gbt.ai;

public class NeoPose {
	
	private float pitch;
	private float roll;
	private float yaw;
	
	public NeoPose(float pitch, float roll, float yaw) {
		super();
		this.pitch = pitch;
		this.roll = roll;
		this.yaw = yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	public float getRoll() {
		return roll;
	}
	public void setRoll(float roll) {
		this.roll = roll;
	}
	public float getYaw() {
		return yaw;
	}
	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
	
	
	@Override
	public String toString() {
		return "Pose [pitch=" + pitch + ", roll=" + roll + ", yaw=" + yaw + "]";
	}

}
