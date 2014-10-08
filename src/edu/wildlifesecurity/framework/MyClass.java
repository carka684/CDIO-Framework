package edu.wildlifesecurity.framework;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

import com.atul.JavaOpenCV.Imshow;

public class MyClass {
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture vc = new VideoCapture("/Users/jonasforsner/Documents/TSBB11/Matlab/CalleHund/im%04d.png");
		 Imshow window1 = new Imshow("Background model");
		System.out.println("Is opened: " + vc.isOpened());
		BackgroundSubtractorMOG2 bgs = new BackgroundSubtractorMOG2(0, 60, true);
		bgs.setInt("nmixtures", 10);
		
		for(int frameNr = 0; frameNr < 1937; frameNr++)
		{
			// Grab & retrieve the next frame
			Mat img = new Mat();
			vc.read(img);
			
			// Create Background Subtractor
			
			Mat fgMask = new Mat();
			//bgs.setInt(name, value);
			if(frameNr < 120)
			{
				bgs.apply(img, fgMask, 0.1);
			}
			else
			{
				bgs.apply(img, fgMask, 0.0001);
				
			}
			
			Mat morphKernel = new Mat();
			morphKernel = Mat.ones(3, 3, CvType.CV_8U);
			Imgproc.erode(fgMask, fgMask, morphKernel);

			Mat convKernel = new Mat();
			convKernel = Mat.ones(5, 5, CvType.CV_8U);
		
			convKernel.mul(convKernel,0.04);
			
			Imgproc.filter2D(fgMask, fgMask, 0, convKernel);
			int threshType = Imgproc.THRESH_BINARY;
			Imgproc.threshold(fgMask, fgMask, 254, 255, threshType);
			
			// Imgproc.dilate(fgMask, fgMask, kernel);
			
			//Imgproc.dilate(fgMask, fgMask, kernel);
			//Imgproc.erode(fgMask, fgMask, kernel);
			
			window1.showImage(fgMask);
		}
	}
}
