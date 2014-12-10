package edu.wildlifesecurity.framework;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.detection.impl.DefaultDetection;

public class TestBGM {
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		int NrOfSavedIm = 0; 
		Vector <Integer> imageHeight = new Vector <Integer>();
		File allfiles = new File("bilder");
		int fileNr = 0;
		for(File file : allfiles.listFiles())
		{
			fileNr++;
			File imgFolder = new File("imagesFileNr" + fileNr);
			imgFolder.mkdir();
			System.out.println(imgFolder.getName());
			
			File bgFolder = new File("bg" + fileNr);
			bgFolder.mkdir();
			
			File orginalFlolder = new File("orginal" + fileNr);
			orginalFlolder.mkdir();
			
			VideoCapture vc = new VideoCapture(file.getPath());
	
			Imshow window1 = new Imshow("Background model");
			Imshow window2 = new Imshow("Filtered background model");
			System.out.println("Is opened: " + vc.isOpened());
			
			IDetection detec = new DefaultDetection();
			Map<String,Object> conf = new HashMap<String, Object>();
			conf.put("Detection_InitTime", 500); // Sets the frame rate when the component should take pictures
			detec.loadConfiguration(conf);
			detec.init();
			int CV_CAP_PROP_FRAME_COUNT = 7;
			Vector<Mat> animalIm;
			for(int frameNr = 0; frameNr < vc.get(CV_CAP_PROP_FRAME_COUNT) - 1; frameNr++)
			{
				// Grab & retrieve the next frame
				Mat img = new Mat();
				vc.read(img);
				
				if(frameNr < 100)
				{
					animalIm = detec.getObjInImage(img);
				}
				else
				{
					if(frameNr % 10 == 0)
					{
						 animalIm = detec.getObjInImage(img);
						 System.out.println(frameNr + " ");
						 if(frameNr % 20 == 0)
						 {
							for(int i = 0; i < animalIm.size(); i++)
							{
								NrOfSavedIm++;
								imageHeight.add(animalIm.get(i).height());
								System.out.println(NrOfSavedIm + " ");
								String imNr = String.format("%05d", NrOfSavedIm);
								Highgui.imwrite(imgFolder.getName() + "/im" + imNr + ".jpg", animalIm.get(i));
							}
							String imNr = String.format("%05d", NrOfSavedIm);
							Highgui.imwrite(orginalFlolder.getName() + "/im" + imNr + ".jpg", img);
						}
					}
				}
				if (frameNr%200 == 0)
				{
					System.gc();
				}
			}
			// System.out.println("Image height mean: " + imageHeight.);
		}
}
}
