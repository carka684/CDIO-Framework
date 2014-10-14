package edu.wildlifesecurity.framework.identification.impl;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvSVM;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;

/**
 * Default implementation of the Identification component
 * 
 * @author Tobias
 *
 */
public class HOGIdentification implements IIdentification {

	/**
	 * Extracts HOG features 
	 * 
	 */
	@Override
	public Mat extractHOGFeatures(Mat inputImage, HOGDescriptor hog,Size s) {
		MatOfFloat features = new MatOfFloat();
		Imgproc.resize(inputImage, inputImage, s);
		hog.compute(inputImage, features);			
		
		return features;
	}
	
	@Override
	public IClassificationResult classify(Mat features,CvSVM SVM) {
		Mat results = new Mat();
		SVM.predict_all(features,results);
		return null;
	}

	@Override
	public Mat extractHOGFeaturesFromFiles(Vector<String> strVec,
			HOGDescriptor hog, Size s) 
	{
		Mat featMat = new Mat();
		for(String file : strVec)
		{
			Mat img = Highgui.imread(file,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			Mat feat = extractHOGFeatures(img, hog,s);
			featMat.push_back(feat.t());
		}
		return featMat;
	}

	

}
