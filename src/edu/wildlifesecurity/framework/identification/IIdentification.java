package edu.wildlifesecurity.framework.identification;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.IComponent;

public interface IIdentification extends IComponent {
	
	void trainClassifier(String pos, String neg);
	
	void evaluateClassifier(String pos, String neg);
	
	IClassificationResult classify(Mat image);
	
	void loadClassifierFromFile(String file);
	
}
