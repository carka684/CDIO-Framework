package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();		
		hogTest.trainClassifier("Dataset/rhino_good/TrainingSet/", "Dataset/other/TrainingSet/", "classifier1.txt");
		hogTest.evaluateClassifier("Dataset/rhino_good/ValidationSet/", "Dataset/other/validation_only_animal/");
		
	}

}
