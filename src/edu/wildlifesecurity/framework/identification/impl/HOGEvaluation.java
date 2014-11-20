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
		
		
		hogTest.loadPrimalVariableFromFile("primalVariableRhinoOther.txt", 0);
		hogTest.loadPrimalVariableFromFile("primalVariableHumanOther.txt", 1);
		hogTest.loadPrimalVariableFromFile("primalVariableRhinoHuman.txt", 2);
		
		
		//long startTime = System.currentTimeMillis();
		//hogTest.evaluateClassifier("/Users/jonasforsner/Documents/Dataset/Validation/", "");
		//long estimatedTime = System.currentTimeMillis() - startTime;
		//System.out.println("Time elapsed: " + estimatedTime + " ms.");
		
		File allfiles = new File("/Users/annasoederroos/TSBB11/Validation/Human");
		//int fileNr = 0;
		for(File file : allfiles.listFiles())
		{
			//fileNr++;
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
			//System.out.println(fileNr + "  ");
			
		}
		System.out.println(hogTest.v);
		int count = 0;
		int countRhino = 0;
		for(int i = 0; i < hogTest.v.size(); i++)
		{
			if(hogTest.v.get(i) == 1)
				count++;
			if(hogTest.v.get(i) == 0)
				countRhino++;
		}
		System.out.println("antal human" + count + "antal Rhino" + countRhino);
		hogTest.v.clear();
		//int fileNr = 0;
		File allfile = new File("/Users/annasoederroos/TSBB11/Validation/Rhino");
		for(File file : allfile.listFiles())
		{
			//fileNr++;
			Mat image = Highgui.imread(file.getPath());
			Mat grayImage = new Mat();
			Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
			hogTest.classify(grayImage);
			//System.out.println(fileNr);
		}
		System.out.println(hogTest.v);
		int count2 = 0;
		int counthuman = 0;
		for(int i = 0; i < hogTest.v.size(); i++)
		{
			if(hogTest.v.get(i) == 0)
				count2++;
			if(hogTest.v.get(i) == 1)
				counthuman++;
		}
		System.out.println("antal Rhino" + count2 + "antal human" + counthuman);
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
		int count3 = 0;
		for(int i = 0; i < hogTest.v.size(); i++)
		{
			if(hogTest.v.get(i) == 2)
				count3++;
		}
		System.out.println("antal unidentified" + count3);

		
		
		
	}


}
