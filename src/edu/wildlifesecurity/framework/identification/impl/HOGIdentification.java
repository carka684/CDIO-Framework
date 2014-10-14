package edu.wildlifesecurity.framework.identification.impl;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;

public class HOGIdentification implements IIdentification {

	/**
	 * Extracts HOG features 
	 * 
	 */
	HOGDescriptor hog;
	CvSVM SVM;
	CvSVMParams params;
	Size s;
	@Override
	public Mat extractFeatures(Mat inputImage) {
		MatOfFloat features = new MatOfFloat();
		Imgproc.resize(inputImage, inputImage, s);
		hog.compute(inputImage, features);			
		
		return features;
	}
	
	@Override
	public IClassificationResult classify(Mat features) {
		Mat results = new Mat();
		SVM.predict_all(features,results);
		return null;
	}

	@Override
	public Mat extractFeaturesFromFiles(Vector<String> strVec){
		Mat featMat = new Mat();
		for(String file : strVec)
		{
			Mat img = Highgui.imread(file,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			Mat feat = extractFeatures(img);
			featMat.push_back(feat.t());
		}
		return featMat;
	}

	@Override
	public CvSVM trainClassifier(Mat trainFeatures, Mat classes) {
		SVM.train(trainFeatures,classes,new Mat(),new Mat(),params);
		return SVM;
	}

}
