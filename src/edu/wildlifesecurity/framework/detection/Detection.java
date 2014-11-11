package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class Detection {
	private Mat image;
	private Rect region;
	private Mat boxImage;
	private int ID;
	
	public Detection(Mat img, Rect rect, Mat boxImage){
		image = img;
		region = rect;
		this.boxImage = boxImage;
		ID = -1;
	}
	public void setID(int ID)
	{
		this.ID = ID;
	}
	public Mat getImg()
	{
		return image;
	}
	public Rect getRegion()
	{
		return region;
	}
	public Mat getBoxImage()
	{
		return boxImage;
	}
	

	
}
