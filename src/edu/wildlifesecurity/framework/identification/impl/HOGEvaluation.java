package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Core;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();
		// hogTest.trainClassifier("Dataset/Training/", "", "classifier1.txt");
		
		hogTest.loadClassifierFromFile("classifier1.txt");
		hogTest.svm_model2primalValue();
		long startTime = System.currentTimeMillis();
		hogTest.evaluateClassifier("Dataset/Validation/", "");
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed: " + estimatedTime + " ms.");
	}

}
