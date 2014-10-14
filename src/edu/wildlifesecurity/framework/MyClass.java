package edu.wildlifesecurity.framework;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

import com.atul.JavaOpenCV.Imshow;

public class MyClass {
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture vc = new VideoCapture("/Users/jonasforsner/Documents/TSBB11/Filmer/Rhinoshort.avi");
		Imshow window1 = new Imshow("Background model");
		Imshow window2 = new Imshow("Filtered background model");
		System.out.println("Is opened: " + vc.isOpened());
		BackgroundSubtractorMOG2 bgs = new BackgroundSubtractorMOG2(0, 30, false);
		bgs.setInt("nmixtures", 5);
		bgs.setDouble("backgroundRatio", 0.9);
		
		int CV_CAP_PROP_FRAME_COUNT = 7;
		//System.out.println("Number of Frames =  " + vc.get(CV_CAP_PROP_FRAME_COUNT));
		
		for(int frameNr = 0; frameNr < vc.get(CV_CAP_PROP_FRAME_COUNT) - 1; frameNr++)
		{
			// Grab & retrieve the next frame
			Mat img = new Mat();
			vc.read(img);
			
			// Create Background Subtractor
			
			Mat fgMask = new Mat();
			//bgs.setInt(name, value);
			if(frameNr < 580)
			{
				bgs.apply(img, fgMask, 0.01);
			}
			else
			{
				bgs.apply(img, fgMask, 0.0001);
			}
			System.out.println(frameNr + "  ");
			
			Mat morphKernel = new Mat();
			morphKernel = Mat.ones(3, 3, CvType.CV_8U);
			Mat fgMaskMod = new Mat();
			Imgproc.erode(fgMask, fgMaskMod, morphKernel);
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			//Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			//Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);
			Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);
			
			Mat contourIm = fgMaskMod.clone();
			
			Vector <MatOfPoint> contours = new Vector <MatOfPoint>();
			Mat contourHierarchy = new Mat();
			
			Imgproc.findContours(contourIm, contours, contourHierarchy, 3, 1);
			
			double th = 500;
			double maxArea = 0;
		
			for (int i = 0; i < contours.size(); i++)
			{
				
				double area;
				area = Imgproc.contourArea(contours.get(i));
				if (area > th && area > maxArea)
				{
					maxArea = area;
					Rect boundBox = Imgproc.boundingRect(contours.get(i));
					Mat obj = img.submat(boundBox).clone();
					Mat mask = fgMaskMod.submat(boundBox).clone();
					Imgproc.threshold(mask, mask, 200, 1, CvType.CV_8U);
					Vector <Mat> channel  = new Vector <Mat>();
					Core.split(obj, channel);
					Vector <Mat> choppedIm = new Vector <Mat>();
					choppedIm.add(channel.get(0).mul(mask));
					choppedIm.add(channel.get(1).mul(mask));
					choppedIm.add(channel.get(2).mul(mask));
					
					Mat resultIm = new Mat();
					Core.merge(choppedIm, resultIm);
					Imgproc.resize(resultIm, resultIm, new Size(480, 480));
					
					window1.showImage(resultIm);
				}
			}
			
			window2.showImage(fgMaskMod);
			
			/*Mat convKernel = new Mat();
			convKernel = Mat.ones(5, 5, CvType.CV_8U);
			convKernel.mul(convKernel,0.04);
			
			Imgproc.filter2D(fgMaskMod, fgMaskMod, 0, convKernel);
			int threshType = Imgproc.THRESH_BINARY;
			Imgproc.threshold(fgMaskMod, fgMaskMod, 254, 255, threshType);*/
			
			
		}
	}
}
