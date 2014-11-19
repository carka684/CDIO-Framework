package edu.wildlifesecurity.framework.identification.impl;

import java.io.File;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class HOGEvaluation {

	public static void main(String[] args) {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		HOGIdentification hogTest = new HOGIdentification(); 
		hogTest.init();
		//hogTest.trainClassifier("/Users/jonasforsner/Documents/Dataset/Training/", "", "primalVariableHumanRhino.txt");
		
		hogTest.loadPrimalVariableFromFile("primalVariableHumanOther.txt", 0);
		hogTest.loadPrimalVariableFromFile("primalVariableRhinoOther.txt", 1);
		
		//hogTest.loadPrimalVariableFromFile("primalVariableRhinoOther.txt", 2);
		
		
		//long startTime = System.currentTimeMillis();
		//hogTest.evaluateClassifier("/Users/jonasforsner/Documents/Dataset/Validation/", "");
		//long estimatedTime = System.currentTimeMillis() - startTime;
		//System.out.println("Time elapsed: " + estimatedTime + " ms.");
		
		File allfiles = new File("/Users/annasoederroos/TSBB11/Validation/Human");
		int fileNr = 0;
		for(File file : allfiles.listFiles())
		{
			fileNr++;
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
			System.out.println(fileNr + "  ");
			
		}
		System.out.println(hogTest.v);
		hogTest.v.clear();
		File allfile = new File("/Users/annasoederroos/TSBB11/Validation/Rhino");
		for(File file : allfile.listFiles())
		{
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
		}
		System.out.println(hogTest.v);
		hogTest.v.clear();
		File allfiless = new File("/Users/annasoederroos/TSBB11/Validation/Other");
		for(File file : allfiless.listFiles())
		{
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
		}
		System.out.println(hogTest.v);

		
		
		
	}


}
