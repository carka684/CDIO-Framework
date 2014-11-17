package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

import org.opencv.core.Mat;

public class DetectionResult {
	private Vector<Detection> detVec;
	private Mat rawDetection;
	public DetectionResult(Vector<Detection> detVec, Mat rawDetection)
	{
		this.detVec = detVec;
		this.rawDetection = rawDetection;
	}
	public Vector<Detection> getVector()
	{
		return detVec;
	}
	public Mat getRawDetection()
	{
		return rawDetection;
	}
}
