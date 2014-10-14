package edu.wildlifesecurity.framework.identification;

import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.IComponent;

public interface IIdentification extends IComponent {

	Mat extractFeatures(Mat inputImage);
	Mat extractFeaturesFromFiles(Vector<String> strVec);
	void trainClassifier(Mat trainFeatures,Mat classes);//Unnecessary, only one line
	IClassificationResult classify(Mat features);
	
}
