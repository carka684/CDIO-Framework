package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

public class Detections {
	private Vector<DetectionResult> detVec;
	
	public Detections(Vector<DetectionResult> detVec)
	{
		this.detVec = detVec;
	}
	
	public Vector<DetectionResult> getVector()
	{
		return detVec;
	}
}
