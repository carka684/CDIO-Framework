package edu.wildlifesecurity.framework.identification;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.IComponent;

public interface IIdentification extends IComponent {

	Mat extractFeatures(Mat inputImage);
	
	IClassificationResult classify(Mat features);
	
}
