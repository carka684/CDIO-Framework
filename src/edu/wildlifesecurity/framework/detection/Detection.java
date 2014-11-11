package edu.wildlifesecurity.framework.detection;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

public class Detection {
	private Mat image;
	private Rect region;
	private Mat boxImage;
	private int ID;
	private Scalar color;
	
	public Detection(Mat img, Rect rect, Mat boxImage){
		image = img;
		region = rect;
		this.boxImage = boxImage;
		ID = -1;
		color = new Scalar(125,125,125);
	}
	public void setColor(Scalar color)
	{
		this.color = color;
	}
	public Scalar getColor()
	{
		return color;
	}
	public int getID()
	{
		return ID;
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
