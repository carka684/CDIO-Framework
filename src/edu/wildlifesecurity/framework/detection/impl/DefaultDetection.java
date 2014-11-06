package edu.wildlifesecurity.framework.detection.impl;

import java.util.Vector;

import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
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
		bgs = new BackgroundSubtractorMOG2(0, 50, false);
		bgs.setInt("nmixtures", 5);
		bgs.setDouble("backgroundRatio", 0.9);
		InitTime = Integer.parseInt(configuration.get("Detection_InitTime").toString());
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
	
	private Rect squarify(Rect boundBox, int imWidth, int imHeight){
		if (boundBox.x - 10 > 0)
		{
			boundBox.x = boundBox.x - 10;
		}
		
		if (boundBox.x + boundBox.width + 20 < imWidth)
		{
			boundBox.width = boundBox.width + 20;
		}
		
		if (boundBox.y - 10 > 0)
		{
			boundBox.y = boundBox.y - 10;
		}
		
		if (boundBox.y + boundBox.height + 20 < imHeight)
		{
			boundBox.height = boundBox.height + 20;
		}
		int side = Math.max(boundBox.width, boundBox.height);
		if(side > Math.min(imWidth, imHeight))
			side = Math.min(imWidth, imHeight);
		
		boundBox.x += boundBox.width/2 - side/2;
		boundBox.y += boundBox.height/2 - side/2;
		
		if(boundBox.x < 0)
			boundBox.x = 0;
		if(boundBox.y < 0)
			boundBox.y = 0;
		if(boundBox.x + side > imWidth)
			boundBox.x = imWidth - side;
		if(boundBox.y + side > imHeight)
			boundBox.y = imHeight - side;
		
		boundBox.width = side;
		boundBox.height = side;
		
		return boundBox;
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
			bgs.apply(img, fgMask, 0.01);
		}
		
		Mat fgMaskMod = openAndClose(fgMask);
		
		Vector <MatOfPoint> contours = new Vector <MatOfPoint>();
		Mat contourHierarchy = new Mat();
	
		try
		{
			Imgproc.findContours(fgMaskMod, contours, contourHierarchy, 3, 1);
		}
		catch(CvException e)
		{
			System.out.println("Error in findcontoures");
		}
		
		
		int MinimalSizeOfObjets = 2000;
		result = getImagesInsideContours(contours, img, MinimalSizeOfObjets);
		return result;
	}
}