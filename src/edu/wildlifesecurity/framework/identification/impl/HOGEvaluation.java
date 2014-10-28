package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Core;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();
		hogTest.trainClassifier("D:/Bilder/DjurEntre/Training/Pos/", "D:/Bilder/DjurEntre/Training/Neg/", "D:/Bilder/DjurEntre/classifier.txt");
		hogTest.evaluateClassifier("D:/Bilder/DjurEntre/Evaluation/Pos/", "D:/Bilder/DjurEntre/Evaluation/Neg/");

	}

}
