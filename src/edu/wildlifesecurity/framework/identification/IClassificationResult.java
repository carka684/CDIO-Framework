package edu.wildlifesecurity.framework.identification;

import org.opencv.core.Mat;

public interface IClassificationResult {
	
	Mat getImage();

	Classes getResultingClass();
	
}