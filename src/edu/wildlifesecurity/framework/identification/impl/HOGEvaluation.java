package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();	
		hogTest.trainClassifier("Dataset/Training/", "", "classifier1.txt");
		hogTest.evaluateClassifier("Dataset/Validation/", "");		
	}

}
