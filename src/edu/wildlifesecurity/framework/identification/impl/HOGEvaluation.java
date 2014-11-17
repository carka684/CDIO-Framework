package edu.wildlifesecurity.framework.identification.impl;

import java.io.File;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();
		hogTest.trainClassifier("/Users/jonasforsner/Documents/Dataset/Training/", "", "primalValue.txt");
		hogTest.loadPrimalValueFromFile("primalValue.txt");
		
		long startTime = System.currentTimeMillis();
		hogTest.evaluateClassifier("/Users/jonasforsner/Documents/Dataset/Validation/", "");
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed: " + estimatedTime + " ms.");
		
		/*File allfiles = new File("/Users/jonasforsner/Documents/Dataset/Validation/Rhino/");
		int fileNr = 0;
		for(File file : allfiles.listFiles())
		{
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
		}*/
	}

}
