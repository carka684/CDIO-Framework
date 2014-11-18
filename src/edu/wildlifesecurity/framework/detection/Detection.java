package edu.wildlifesecurity.framework.detection;

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import edu.wildlifesecurity.framework.identification.Classes;

public class Detection {
	private Rect region;
	private Mat regionImage;
	private int ID;
	private Scalar color;
	private Classes classification;
	
	
	public Detection(Rect rect, Mat regionImage){
		region = rect;
		this.regionImage = regionImage;
		ID = -1;
		color = new Scalar(125,125,125);
		classification = Classes.UNIDENTIFIED;
	}
	public void setClassification(Classes classification)
	{
		this.classification = classification;
	}
	public Classes getClassification()
	{
		return this.classification;
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
	public Rect getRegion()
	{
		return region;
	}
	public Mat getRegionImage()
	{
		return regionImage;
	}
	

	
}
