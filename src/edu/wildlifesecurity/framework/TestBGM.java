package edu.wildlifesecurity.framework;

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
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		int NrOfSavedIm = 0; 

		
		VideoCapture vc = new VideoCapture("bilder/animalEntre.avi");

		Imshow window1 = new Imshow("Background model");
		Imshow window2 = new Imshow("Filtered background model");
		System.out.println("Is opened: " + vc.isOpened());
		
		IDetection detec = new DefaultDetection();
		Map<String,Object> conf = new HashMap<String, Object>();
		conf.put("Detection_InitTime", 500); // Sets the frame rate when the component should take pictures
		detec.loadConfiguration(conf);
		detec.init();
		int CV_CAP_PROP_FRAME_COUNT = 7;
		for(int frameNr = 0; frameNr < vc.get(CV_CAP_PROP_FRAME_COUNT) - 1; frameNr++)
		{
			// Grab & retrieve the next frame
			Mat img = new Mat();
			vc.read(img);
			Vector<Mat> animalIm= detec.getObjInImage(img);
			
			for(int i = 0; i <animalIm.size(); i++)
			{
				NrOfSavedIm++;
				String imNr = String.format("%05d", NrOfSavedIm);
				Highgui.imwrite("DjurEntre/im" + imNr + ".jpg", animalIm.get(i));	
			}
		}
	}
}
