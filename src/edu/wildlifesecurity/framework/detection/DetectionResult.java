package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class DetectionResult {
	
	public DetectionResult(){
		images = new Vector<Mat>();
		regions = new Vector<Rect>();
	}

	public Vector<Mat> images;
	
	public Vector<Rect> regions;
	
	public Mat rawDetection;
	
}
