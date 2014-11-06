package edu.wildlifesecurity.framework.identification.impl;

import java.util.Map;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
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
		int cols  = (int) extractFeatures(Highgui.imread(trainFiles.elementAt(0),Highgui.CV_LOAD_IMAGE_GRAYSCALE)).size().height;
		int rows = trainFiles.size();
		Mat featMat =  Mat.zeros(rows,cols,CvType.CV_32F);

		int counter = 0;
		for(String file : trainFiles)
		{
			Mat img = new Mat();
			Mat feat = new Mat();
			img = Highgui.imread(file,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			feat = extractFeatures(img);
			Mat tmp = featMat.submat(counter, counter+1,0,cols);
			counter++;
			Core.add(feat.t(), tmp, tmp);
			tmp.release();
			img.release();
			feat.release();
		}
		return featMat;
	}

	@Override
	public void trainClassifier(String trainFolder,String UNUSED, String outputFile) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(trainFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat featMat = extractFeaturesFromFiles(trainFiles);
		
		SVM.train(featMat,classes,new Mat(),new Mat(),params);
		SVM.save(outputFile);
	}
	
	@Override
	public void evaluateClassifier(String valFolder, String UNUSED) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(valFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();  
		Mat classes = trainReader.getClasses();
		Mat featMat = extractFeaturesFromFiles(trainFiles);
		
		Mat results = new Mat();
		SVM.predict_all(featMat, results);
		System.out.println("Truth:  " + classes.t().dump());
		System.out.println("Result: " + results.t().dump());
		double[] res = getResult(classes, results,trainReader.getNumOfClasses(),trainReader.getNumOfEachClass());
		//System.out.println("TP: " + res[0] + " FN: " + res[1]  + " TN: " + res[2] + " FP: " + res[3]);

	}
	/*
	 * TODO: Get result working as it should for multiclass SVM
	 */
	public static  double[] getResult(Mat classes, Mat result, int numOfClasses,int[] numOfEachClass)
	{
		return null;
	}
	
	@Override
	public void loadClassifierFromFile(String file) {
			SVM.load(file);
	}

	
	

}
