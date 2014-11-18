package edu.wildlifesecurity.framework.tracking;

import java.util.Date;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import edu.wildlifesecurity.framework.identification.Classes;

public class Capture {

	public Classes classification;
	public Date timeStamp;
	public Mat regionImage;
	public String GPSPos;
	
	public Capture(Date timeStamp, Mat regionImage, Classes classification, String GPSPos) {
		this.timeStamp = timeStamp;
		this.regionImage = regionImage;
		this.classification = classification;
		this.GPSPos = GPSPos;
	}

	


}
