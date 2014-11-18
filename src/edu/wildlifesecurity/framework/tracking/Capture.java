package edu.wildlifesecurity.framework.tracking;

import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class Capture {

	public int captureId;
	public Date timeStamp;
	public int trapDeviceId;
	public Rect region;
	public Mat regionImage;
	public int classification;
	public String GPSPos;
	
	public Capture(int captureId, Date timeStamp, int trapDeviceId,
			Rect region, Mat regionImage, int classification, String GPSPos) {
		this.captureId = captureId;
		this.timeStamp = timeStamp;
		this.trapDeviceId = trapDeviceId;
		this.region = region;
		this.regionImage = regionImage;
		this.classification = classification;
		this.GPSPos = GPSPos;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getTrapDeviceId() {
		return trapDeviceId;
	}

	public void setTrapDeviceId(int trapDeviceId) {
		this.trapDeviceId = trapDeviceId;
	}
	public int getCaptureId() {
		return captureId;
	}
	public void setCaptureId(int captureId) {
		this.captureId = captureId;
	}
	public Rect getRegion() {
		return region;
	}
	public void setRegion(Rect region) {
		this.region = region;
	}
	public Mat getRegionImage() {
		return regionImage;
	}
	public void setRegionImage(Mat regionImage) {
		this.regionImage = regionImage;
	}
	public int getClassification() {
		return classification;
	}
	public void setClassification(int classification) {
		this.classification = classification;
	}
	public String getGPSPos() {
		return GPSPos;
	}
	public void setGPSPos(String gPSPos) {
		GPSPos = gPSPos;
	}


}
