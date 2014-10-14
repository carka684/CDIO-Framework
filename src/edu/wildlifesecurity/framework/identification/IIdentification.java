package edu.wildlifesecurity.framework.identification;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.ml.CvSVM;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.IComponent;

public interface IIdentification extends IComponent {

	Mat extractHOGFeatures(Mat inputImage,HOGDescriptor hog,Size s);
	Mat extractHOGFeaturesFromFiles(Vector<String> strVec, HOGDescriptor hog,Size s);
	IClassificationResult classify(Mat features,CvSVM SVM);
	
}
