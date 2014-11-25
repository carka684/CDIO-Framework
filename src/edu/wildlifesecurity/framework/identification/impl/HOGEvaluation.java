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
		hogTest.trainClassifier("/Users/jonasforsner/Documents/Dataset/Training/", "", "primalVariableTest.txt");
		
		
		//hogTest.loadPrimalVariableFromFile("primalVariableRhinoOther.txt", 0);
		//hogTest.loadPrimalVariableFromFile("primalVariableHumanOther.txt", 1);
		//hogTest.loadPrimalVariableFromFile("primalVariableRhinoHuman.txt", 2);
		
		
		//long startTime = System.currentTimeMillis();
		hogTest.evaluateClassifier("/Users/jonasforsner/Documents/Dataset/Validation/", "");
		//long estimatedTime = System.currentTimeMillis() - startTime;
		//System.out.println("Time elapsed: " + estimatedTime + " ms.");
		
		// Validate on Human
		/*File allfiles = new File("/Users/jonasforsner/Documents/Dataset/Validation/Human/");
		for(File file : allfiles.listFiles())
		{
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
		}
		System.out.println(hogTest.v);
		int countHuman = 0;
		int countRhino = 0;
		for(int i = 0; i < hogTest.v.size(); i++)
		{
			if(hogTest.v.get(i) == Classes.HUMAN)
				countHuman++;
			if(hogTest.v.get(i) == Classes.RHINO)
				countRhino++;
		}
		System.out.println("Antal human: " + countHuman + ", antal Rhino: " + countRhino);
		hogTest.v.clear();
		
		// Validate on Rhino
		File allfile = new File("/Users/jonasforsner/Documents/Dataset/Validation/Rhino/");
		for(File file : allfile.listFiles())
		{
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
		}
		System.out.println(hogTest.v);
		int countRhino2 = 0;
		int countHuman2 = 0;
		for(int i = 0; i < hogTest.v.size(); i++)
		{
			if(hogTest.v.get(i) == Classes.RHINO)
				countRhino2++;
			if(hogTest.v.get(i) == Classes.HUMAN)
				countHuman2++;
		}
		System.out.println("Antal Rhino: " + countRhino2 + ", antal human: " + countHuman2);
		hogTest.v.clear();
		
		// Validate on Other
		File allfiless = new File("/Users/jonasforsner/Documents/Dataset/Validation/Other/");
		for(File file : allfiless.listFiles())
		{
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
		}
		System.out.println(hogTest.v);
		int countUnidentified = 0;
		for(int i = 0; i < hogTest.v.size(); i++)
		{
			if(hogTest.v.get(i) == Classes.UNIDENTIFIED)
				countUnidentified++;
		}
		System.out.println("Antal unidentified: " + countUnidentified);*/
	}
}