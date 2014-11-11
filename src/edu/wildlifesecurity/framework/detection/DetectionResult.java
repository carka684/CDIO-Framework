package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

public class DetectionResult {
	private Vector<Detection> detVec;
	
	public DetectionResult(Vector<Detection> detVec)
	{
		this.detVec = detVec;
	}
	
	public Vector<Detection> getVector()
	{
		return detVec;
	}
}
