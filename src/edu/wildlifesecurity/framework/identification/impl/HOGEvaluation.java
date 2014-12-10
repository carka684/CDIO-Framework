package edu.wildlifesecurity.framework.identification.impl;

import java.io.File;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import edu.wildlifesecurity.framework.identification.Classes;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();
		
		// Training and evaluating a classifier with two classes
		//hogTest.trainClassifier("/Users/jonasforsner/Documents/Dataset/Training/", "", "wRhinoHuman.txt");
		//hogTest.evaluateTrainClassifier("/Users/jonasforsner/Documents/Dataset/Validation/", "");
		
		// Classify images using the multiclass classifier
		hogTest.loadPrimalVariableFromFile("wRhinoOther.txt", 0);
		//hogTest.loadPrimalVariableFromFile("wHumanOther.txt", 1);
		//hogTest.loadPrimalVariableFromFile("wRhinoHuman.txt", 2);
		hogTest.evaluateClassifier("/Users/jonasforsner/Documents/Dataset/Validation/");
	}
}