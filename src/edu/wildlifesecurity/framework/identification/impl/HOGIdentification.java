package edu.wildlifesecurity.framework.identification.impl;

import java.util.Map;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;


public class HOGIdentification extends AbstractComponent implements IIdentification {

	/**
	 * Extracts HOG features 
	 * 
	 */
	HOGDescriptor hog;
	CvSVM SVM;
	CvSVMParams params;
	Size s;
	
	@Override
	public void init(){
		// TODO: Should be loaded from configuration
		s = new Size(480,480);
		hog = new HOGDescriptor(s,new Size(16,16),new Size(8,8),new Size(8,8),9,-1,0.2,1,1,false,64);
		SVM = new CvSVM();
		params = new CvSVMParams();
	    params.set_kernel_type(CvSVM.LINEAR);
	    
	    // TODO: Load pre-trained classifier
	}

	public Mat extractFeatures(Mat inputImage) {
		MatOfFloat features = new MatOfFloat();
		Imgproc.resize(inputImage, inputImage, s);
		hog.compute(inputImage, features);			
		return features;
	}
	
	@Override
	public IClassificationResult classify(Mat image) {

		Mat features = extractFeatures(image);
		float res = SVM.predict(features);
		Classes resClass = (res < 0)?Classes.UNIDENTIFIED:Classes.RHINO;
		
		return new ClassificationResult(resClass);
	}

	public Mat extractFeaturesFromFiles(Vector<String> trainFiles){
		Mat featMat = new Mat();
		for(String file : trainFiles)
		{
			Mat img = new Mat();
			Mat feat = new Mat();
			img = Highgui.imread(file,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			feat = extractFeatures(img);
			featMat.push_back(feat.t());
		}
		return featMat;
	}

	@Override
	public void trainClassifier(String pos, String neg, String outputFile) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(pos,neg);
		Vector<String> trainFiles = trainReader.getFiles();
		Mat classes = trainReader.getClasses();
		Mat featMat = extractFeaturesFromFiles(trainFiles);
		
		SVM.train(featMat,classes,new Mat(),new Mat(),params);
		SVM.save(outputFile);
	}
	
	@Override
	public void evaluateClassifier(String pos, String neg) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(pos,neg);
		Vector<String> trainFiles = trainReader.getFiles();
		Mat classes = trainReader.getClasses();
		Mat featMat = extractFeaturesFromFiles(trainFiles);
		
		Mat results = new Mat();
		SVM.predict_all(featMat, results);
		
		double[] res = getResult(classes, results);
		System.out.println("TP: " + res[0] + " FN: " + res[1]  + " TN: " + res[2] + " FP: " + res[3]);

	}
	
	public static  double[] getResult(Mat classes, Mat result)
	{
		Mat falseNegMat = new Mat();
		Mat falsePosMat = new Mat();
		Mat tempClass = new Mat();
		Core.add(classes,new Scalar(1),tempClass);
		int numberOfPos = Core.countNonZero(tempClass);
		int numberOfNeg = (int) classes.total() - numberOfPos;
		Core.absdiff(classes.rowRange(0, numberOfPos),result.rowRange(0, numberOfPos),falseNegMat);
		Core.absdiff(classes.rowRange(numberOfPos,numberOfPos+numberOfNeg),result.rowRange(numberOfPos, numberOfPos+numberOfNeg),falsePosMat);
		
		Scalar falseNegRes =  Core.sumElems(falseNegMat);
		Scalar falsePosRes =  Core.sumElems(falsePosMat);
		double FN = falseNegRes.mul(new Scalar((double) 1/(2*numberOfPos))).val[0];
		double TP = 1-FN;
		double FP = falsePosRes.mul(new Scalar((double) 1/(2*numberOfPos))).val[0];
		double TN  = 1 - FP;
		double[] res = {TP,FN,TN,FP};
		return res;	
	}
	
	@Override
	public void loadClassifierFromFile(String file) {
			SVM.load(file);
	}

	
	

}
