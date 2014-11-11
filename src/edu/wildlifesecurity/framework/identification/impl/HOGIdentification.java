package edu.wildlifesecurity.framework.identification.impl;

import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.EventDispatcher;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.identification.IdentificationEvent;


public class HOGIdentification extends AbstractComponent implements IIdentification {

	/**
	 * Extracts HOG features 
	 * 
	 */
	HOGDescriptor hog;
	svm SVM;
	svm_model model;
	svm_parameter params;
	//CvSVM SVM;
	//CvSVMParams params;
	Size s;
	
	private EventDispatcher<IdentificationEvent> dispatcher =  new EventDispatcher<IdentificationEvent>();
	
	@Override
	public ISubscription addEventHandler(EventType type, IEventHandler<IdentificationEvent> handler){
		return dispatcher.addEventHandler(type, handler);
	}
	
	@Override
	public void init(){
		// TODO: Should be loaded from configuration
		s = new Size(240,240);
		hog = new HOGDescriptor(s,new Size(16,16),new Size(8,8),new Size(8,8),9);
		SVM = new svm(); 
		model = new svm_model();
		params = new svm_parameter();
		int linearSVM = 0;
		params.kernel_type = linearSVM;
		
		//SVM = new CvSVM();
		//params = new CvSVMParams();
		//params.set_kernel_type(CvSVM.LINEAR);

	    
	    // Load classifier
	    //loadClassifierFromFile(configuration.get("Identification_Classifier").toString());
	}

	public Mat extractFeatures(Mat inputImage) {
		MatOfFloat features = new MatOfFloat();
		Imgproc.resize(inputImage, inputImage, s);
		hog.compute(inputImage, features);	
		//System.out.println(features.size());
		return features;
	}
	
	@Override
	public IClassificationResult classify(Mat image) {
		Mat features = extractFeatures(image);
		// Ny version med libsvm
		svm_node[] imageNodes = mat2svm_nodeArray(features, 0);
		float res = (float) SVM.svm_predict(model, imageNodes);
		// Förra versionen
		//float res = SVM.predict(features);
		Classes resClass = (res < 0)?Classes.UNIDENTIFIED:Classes.RHINO;
		
		ClassificationResult result = new ClassificationResult(resClass, image);
		dispatcher.dispatch(new IdentificationEvent(IdentificationEvent.NEW_IDENTIFICATION, result));
		return result;
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
			try {
				img = Highgui.imread(file,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			} catch (Exception e) {
				System.out.println("F�rs�kte l�sa in:" + file);
				e.printStackTrace();
			}
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
		// System.out.println("FeatureMatrix size: " + featMat.rows() + ", " + featMat.cols());
		// Ny version med libsvm
		svm_problem prob = mat2svm_problem(featMat, classes);	
		/*System.out.println("Prob, l = " + prob.l);
		System.out.println("y length = " + prob.y.length);
		System.out.println("svm_node = " + prob.x[0].length + " sista " + prob.x[279].length); */
		model = SVM.svm_train(prob, params);

		// Förra versionen
		//SVM.train(featMat,classes,new Mat(),new Mat(),params);
		//SVM.save(outputFile);
	}
	
	@Override
	public void evaluateClassifier(String valFolder, String UNUSED) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(valFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat featMat = extractFeaturesFromFiles(trainFiles);
		
		// Ny version med libsvm
		Mat results = new Mat(featMat.rows(), 1, CvType.CV_8U);
		for(int row = 0; row < featMat.rows(); row++) {
			svm_node[] tempNodes = mat2svm_nodeArray(featMat, row);
			//System.out.println("tempNode size = " + tempNodes.length);
			double sampleClass = SVM.svm_predict(model, tempNodes);
			results.put(row, 0, sampleClass);
		}
		
		// Förra versionen
		//SVM.predict_all(featMat, results);
		/*System.out.println("Resultvector: ");
		for(int i=0; i < results.rows(); i++){
			System.out.println(results.get(i,0)[0]);
		}*/
		double[] res = getResult(classes, results,trainReader.getNumOfClasses(),trainReader.getNumOfEachClass());
	}
	/*
	 * TODO: How should the result be presented?
	 */
	public static  double[] getResult(Mat classes, Mat results, int numOfClasses,int[] numOfEachClass)
	{
		int pos = 0;
		for(int i = 0; i < numOfClasses; i++)
		{
			Mat temp = results.submat(pos, pos+numOfEachClass[i], 0, 1).clone();
			pos += numOfEachClass[i];
			Core.subtract(temp, new Scalar(i), temp);
			int numOfTP = numOfEachClass[i] - Core.countNonZero(temp);
			Core.subtract(results,new Scalar(i),temp);
			int numOfFN = (classes.rows() - Core.countNonZero(temp)) - numOfTP;
			System.out.println("Class " + i + ":");
			System.out.println("TP: " + (double) numOfTP/numOfEachClass[i]);
			System.out.println("FN: " + (double) numOfFN/(classes.rows()-numOfEachClass[i]));
		}
		return null;
	}
	
	@Override
	public void loadClassifierFromFile(String file) {
		//SVM.load(file);
	}
	
	// Hjälpfunktioner för att libsvm varianten ska fungera
	public svm_problem mat2svm_problem(Mat featureMat, Mat classes) {
		svm_problem result = new svm_problem();
		result.l = featureMat.rows();
		//float[] tempClasses = classes.toArray();
		result.y = new double[featureMat.rows()];
		svm_node[][] svmNodes = new svm_node[featureMat.rows()][featureMat.cols()];
		for (int row = 0; row < featureMat.rows(); row++)  {
			result.y[row] = classes.get(row, 0)[0];
			for(int col = 0; col < featureMat.cols(); col++) {
				svm_node tempNode = new svm_node();
				if (col != featureMat.cols() - 1) {
					tempNode.index = col;
				}
				else {
					tempNode.index = -1;
				}
				tempNode.value = featureMat.get(row, col)[0];
				svmNodes[row][col] = tempNode;
			}
		}
		result.x = svmNodes;
		return result;
	}
	
	public svm_node[] mat2svm_nodeArray(Mat featureMat, int sampleNr) {
		svm_node[] result = new svm_node[featureMat.cols()];
		for(int col = 0; col < featureMat.cols(); col++) {
			svm_node tempNode = new svm_node();
			if (col != featureMat.cols() - 1) {
				tempNode.index = col;
			}
			else {
				tempNode.index = -1;
			}
			tempNode.value = featureMat.get(sampleNr, col)[0];
			result[col] = tempNode; 
		}
		
		return result;
	}
}
