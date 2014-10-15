package edu.wildlifesecurity.framework.identification.impl;

import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;

/**
 * Default implementation of the Identification component
 * 
 * @author Tobias
 *
 */
public class HOGIdentification extends AbstractComponent implements IIdentification {

	/**
	 * Extracts HOG features 
	 * 
	 */
	@Override
	public Mat extractFeatures(Mat inputImage) {
		
		HOGDescriptor descriptor = new HOGDescriptor();
		MatOfFloat features = new MatOfFloat();
		MatOfPoint locations = new MatOfPoint();
		descriptor.compute(inputImage, features, new Size(32,32), new Size(0,0), locations);
		
		return features;
	}

	@Override
	public IClassificationResult classify(Mat features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadConfiguration(Map<String, Object> config) {
		// TODO Auto-generated method stub
		
	}

}
