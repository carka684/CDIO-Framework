package edu.wildlifesecurity.framework.detection;

import java.util.Vector;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.IComponent;

// input: raw image 
//output: image of animal 
public interface IDetection extends IComponent {
	 
	Vector<Mat> getAnimalsInImage(Mat image, int frameNr);

}
