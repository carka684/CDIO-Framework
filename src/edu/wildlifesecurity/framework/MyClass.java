package edu.wildlifesecurity.framework;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

import com.atul.JavaOpenCV.Imshow;

public class MyClass {
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture vc = new VideoCapture("C:/Camera1_2.mp4");

		Imshow window1 = new Imshow("Background model");
		Imshow window2 = new Imshow("Filtered background model");
		System.out.println("Is opened: " + vc.isOpened());
		BackgroundSubtractorMOG2 bgs = new BackgroundSubtractorMOG2(0, 50, false);

		bgs.setInt("nmixtures", 5);
		bgs.setDouble("backgroundRatio", 0.9);
		
		int CV_CAP_PROP_FRAME_COUNT = 7;

		int NrOfSavedIm = 0;
		for(int frameNr = 0; frameNr < vc.get(CV_CAP_PROP_FRAME_COUNT) - 1; frameNr++)
		{
			// Grab & retrieve the next frame
			Mat img = new Mat();
			vc.read(img);
			
			// Create Background Subtractor
			
			Mat fgMask = new Mat();
			//bgs.setInt(name, value);
			if(frameNr < 280)
			{
				bgs.apply(img, fgMask, 0.01);
			}
			else
			{
				bgs.apply(img, fgMask, 0.0001);
			}
			System.out.println(frameNr + " ");
			
			Mat morphKernel = new Mat();
			morphKernel = Mat.ones(3, 3, CvType.CV_8U);
			Mat fgMaskMod = new Mat();
			Imgproc.erode(fgMask, fgMaskMod, morphKernel);
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			Imgproc.dilate(fgMaskMod, fgMaskMod, morphKernel);
			Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);
			Imgproc.erode(fgMaskMod, fgMaskMod, morphKernel);
			
			Mat contourIm = fgMaskMod.clone();
			
			List <MatOfPoint> contours = new ArrayList <MatOfPoint>();
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
					NrOfSavedIm++;
					maxArea = area;
					Rect boundBox = Imgproc.boundingRect(contours.get(i));
					if (boundBox.x - 10 > 0)
					{
						boundBox.x = boundBox.x - 10;
					}
					
					if (boundBox.x + boundBox.width + 20 < img.width())
					{
						boundBox.width = boundBox.width + 20;
					}
					
					if (boundBox.y - 10 > 0)
					{
						boundBox.y = boundBox.y - 10;
					}
					
					if (boundBox.y + boundBox.height + 20 < img.height())
					{
						boundBox.height = boundBox.height + 20;
					}
					
					boundBox = squarify(boundBox, img.width(), img.height());
					Mat objIm = img.submat(boundBox).clone();
					
					Mat cIm = new Mat(img.size(), CvType.CV_8U);
					Imgproc.drawContours(cIm, contours, i, new Scalar(255,255,255), -1);
				
					Mat mask = fgMaskMod.submat(boundBox).clone();
					Imgproc.threshold(mask, mask, 200, 1, CvType.CV_8U);
					
					Vector<Mat> channel = new Vector<Mat>();
					Core.split(objIm, channel);
					Vector <Mat> choppedIm = new Vector <Mat>();
					choppedIm.add(channel.get(0).mul(mask));
					choppedIm.add(channel.get(1).mul(mask));
					choppedIm.add(channel.get(2).mul(mask));
					
					Mat resultIm = new Mat();
					Core.merge(choppedIm, resultIm);
					String imNr = String.format("%05d", NrOfSavedIm);
					// System.out.println(imNr + " ");
					Highgui.imwrite("C:/Users/Tobias/Pictures/DjurEntre/im" + imNr + ".jpg", objIm);
				}
			}
			Mat imgsmall = new Mat();
			Imgproc.resize(img, imgsmall, new Size(img.width()/3, img.height()/3));
			window1.showImage(imgsmall);
			Imgproc.resize(fgMaskMod, imgsmall, new Size(fgMaskMod.width()/3, fgMaskMod.height()/3));
			window2.showImage(imgsmall);			
		}
	}
	
	private static Rect squarify(Rect rect, int imWidth, int imHeight){
		int side = Math.max(rect.width, rect.height);
		if(side > Math.min(imWidth, imHeight))
			side = Math.min(imWidth, imHeight);
		
		rect.x += rect.width/2 - side/2;
		rect.y += rect.height/2 - side/2;
		
		if(rect.x < 0)
			rect.x = 0;
		if(rect.y < 0)
			rect.y = 0;
		if(rect.x + side > imWidth)
			rect.x = imWidth - side;
		if(rect.y + side > imHeight)
			rect.y = imHeight - side;
		
		rect.width = side;
		rect.height = side;
		
		return rect;
	}
}
