package edu.wildlifesecurity.framework.tracking;

import java.util.Date;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.identification.Classes;

public class Capture {
	
	public int captureId;
	
	public Classes type;

	public Date timeStamp;
	
	public int trapDeviceId;
	
	public String position;
	
	public Mat image;
	
}
