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
	public double longitude;
	public double latitude;
	public int heading;
	
	public Capture(){ }
	
	public Capture(Date timeStamp, Mat regionImage, Classes classification, double latitude, double longitude, int heading) {
		this.timeStamp = timeStamp;
		this.regionImage = regionImage;
		this.classification = classification;
		this.longitude = longitude;
		this.latitude  = latitude;
		this.heading = heading;
	}
	
	public Capture(Integer id,Integer trapDeviceId,Date timeStamp, Mat regionImage, Classes classification, double latitude, 
			double longitude, int heading) {
		this.id=id;
		this.trapDeviceId=trapDeviceId;
		this.timeStamp = timeStamp;
		this.regionImage = regionImage;
		this.classification = classification;
		this.longitude = longitude;
		this.latitude  = latitude;
		this.heading = heading;

	}

	


}
