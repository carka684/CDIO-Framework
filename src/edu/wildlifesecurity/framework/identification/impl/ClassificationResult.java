package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IClassificationResult;

public class ClassificationResult implements IClassificationResult {

	private Classes resultClass;
	private Mat image;
	
	public ClassificationResult(Classes resultClass, Mat image) {
		this.resultClass = resultClass;
		this.image = image;
	}
	
	@Override
	public Classes getResultingClass() {
		return resultClass;
	}
	
	public Mat getImage(){
		return image;
	}

}
