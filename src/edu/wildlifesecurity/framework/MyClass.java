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
		
		VideoCapture vc = new VideoCapture("/Users/jonasforsner/Documents/TSBB11/Matlab/CalleHundLong/im%04d.png");
		 Imshow window1 = new Imshow("Background model");
		Imshow window2 = new Imshow("Filtered background model");
		System.out.println("Is opened: " + vc.isOpened());
		BackgroundSubtractorMOG2 bgs = new BackgroundSubtractorMOG2(0, 60, true);
		bgs.setInt("nmixtures", 3);
		bgs.setDouble("backgroundRatio", 0.9);
		
		for(int frameNr = 0; frameNr < 2776; frameNr++)
		{
			// Grab & retrieve the next frame
			Mat img = new Mat();
			vc.read(img);
			
			// Create Background Subtractor
			
			Mat fgMask = new Mat();
			//bgs.setInt(name, value);
			if(frameNr < 1000)
			{
				bgs.apply(img, fgMask, 0.01);
			}
			else
			{
				bgs.apply(img, fgMask, 0.00001);
				
			}
			System.out.println(frameNr + "  ");
			
			
			Mat morphKernel = new Mat();
			morphKernel = Mat.ones(3, 3, CvType.CV_8U);
			Mat fgMaskMod = new Mat();
			Imgproc.erode(fgMask, fgMaskMod, morphKernel);
			//Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);
			//Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel); 
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel); 
			//Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			//Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);
			Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);

			Mat convKernel = new Mat();
			convKernel = Mat.ones(5, 5, CvType.CV_8U);
		
			convKernel.mul(convKernel,0.04);
			
			Imgproc.filter2D(fgMaskMod, fgMaskMod, 0, convKernel);
			int threshType = Imgproc.THRESH_BINARY;
			Imgproc.threshold(fgMaskMod, fgMaskMod, 254, 255, threshType);
			
			
			window1.showImage(fgMask);
			window2.showImage(fgMaskMod);
		}
	}
}
