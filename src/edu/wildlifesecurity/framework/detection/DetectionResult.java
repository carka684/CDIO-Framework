package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

import org.opencv.core.Mat;

public class DetectionResult {
	private Vector<Detection> detVec;
	private Mat rawDetection;
	private Mat orginalImage;
	
	public DetectionResult(Vector<Detection> detVec, Mat rawDetection,Mat orignialImage)
	{
		this.detVec = detVec;
		this.rawDetection = rawDetection;
		this.orginalImage = orignialImage;
	}
	public Vector<Detection> getVector()
	{
		return detVec;
	}
	public Mat getRawDetection()
	{
		return rawDetection;
	}
	public Mat getOriginalImage()
	{
		return orginalImage;
	}
	
}
