package edu.wildlifesecurity.framework.detection.impl;

import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.detection.IDetection;

public class DefaultDetection extends AbstractComponent implements IDetection
{
	private BackgroundSubtractorMOG2 bgs;
	private int InitTime;
	private int age;
	
	@Override
	public void init()
	{
		bgs = new BackgroundSubtractorMOG2(0, 20, false);
		InitTime = (Integer)configuration.get("Detection_InitTime");
	}
	
	private Vector<Mat> getImagesInsideContours(Vector <MatOfPoint> contours, Mat img, int minAreaOnImage)
	{
		Vector <Mat> result = new Vector<Mat>();
		for (int i = 0; i < contours.size(); i++)
		{
			double area = Imgproc.contourArea(contours.get(i));
			if (area > minAreaOnImage)
			{
				Rect boundBox = Imgproc.boundingRect(contours.get(i));
				boundBox = squarify(boundBox, img.width(), img.height());
				result.add(img.submat(boundBox).clone());
			}
		}
		return result;
	}
	
	private Rect squarify(Rect rect, int imWidth, int imHeight){
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
	
	private Mat openAndClose(Mat binaryImage)
	{
		Mat morphKernel = new Mat();
		Mat modifiedImage = new Mat();
		morphKernel = Mat.ones(3, 3, CvType.CV_8U);
		
		Imgproc.erode(binaryImage, modifiedImage, morphKernel);
		Imgproc.dilate(modifiedImage, modifiedImage, morphKernel);
		Imgproc.dilate(modifiedImage, modifiedImage, morphKernel);
		Imgproc.dilate(modifiedImage, modifiedImage, morphKernel);
		Imgproc.erode(modifiedImage, modifiedImage, morphKernel);
		Imgproc.erode(modifiedImage, modifiedImage, morphKernel);
		return modifiedImage;
	}
	
		
	@Override
	public Vector<Mat> getObjInImage(Mat img)
	{
		age++;
		Vector <Mat> result;
		Mat fgMask = new Mat();
	
		if(age < InitTime)	
		{
			bgs.apply(img, fgMask, 0.01);
		}
		else
		{
			bgs.apply(img, fgMask, 0.001);
		}
		
		Mat fgMaskMod = openAndClose(fgMask);
		
		Vector <MatOfPoint> contours = new Vector <MatOfPoint>();
		Mat contourHierarchy = new Mat();
	
		Imgproc.findContours(fgMaskMod, contours, contourHierarchy, 3, 1);
		
		int MinimalSizeOfObjets = 500;
		result = getImagesInsideContours(contours, img, MinimalSizeOfObjets);
		return result;
	}
}