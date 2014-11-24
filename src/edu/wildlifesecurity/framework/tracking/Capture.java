package edu.wildlifesecurity.framework.tracking;

import java.util.Date;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.identification.Classes;

public class Capture {

	public Integer id = 0;
	public Integer trapDeviceId = 0;
	public Classes classification;
	public Date timeStamp;
	public Mat regionImage;
	public String GPSPos = "";
	
	public Capture(){ }
	
	public Capture(Date timeStamp, Mat regionImage, Classes classification, String GPSPos) {
		this.timeStamp = timeStamp;
		this.regionImage = regionImage;
		this.classification = classification;
		this.GPSPos = GPSPos;
	}
	
	public Capture(Integer id,Integer trapDeviceId,Date timeStamp, Mat regionImage, Classes classification, String GPSPos) {
		this.id=id;
		this.trapDeviceId=trapDeviceId;
		this.timeStamp = timeStamp;
		this.regionImage = regionImage;
		this.classification = classification;
		this.GPSPos = GPSPos;
	}

	


}
