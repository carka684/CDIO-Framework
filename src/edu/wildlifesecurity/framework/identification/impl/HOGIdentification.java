package edu.wildlifesecurity.framework.identification.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
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
	Vector<Double> w = new Vector<Double>();
	
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
		params.C = 16;
		params.eps = 0.01;
		
		//SVM = new CvSVM();
		//params = new CvSVMParams();
		//params.set_kernel_type(CvSVM.LINEAR);

	    
	    // Load classifier
		//loadPrimalValueFromFile((configuration.get("Identification_Classifier").toString()));
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
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);
		Mat features = extractFeatures(grayImage);
		// Ny version med libsvm
		svm_node[] imageFeatureNodes = mat2svm_nodeArray(features.t(), 0); // features must be a row-vector
		double res = svm_plane_predict(imageFeatureNodes); // classify using the plane
		// double res = svm.svm_predict(model, imageFeatureNodes); // libsvm version
		// Förra versionen
		//float res = SVM.predict(features);
		//System.out.print(", " + (int) res);
		Classes resClass = (res >= 1)?Classes.RHINO:Classes.UNIDENTIFIED;
		
		ClassificationResult result = new ClassificationResult(resClass, grayImage);
		// System.out.println("ResultingClass: " + result.getResultingClass());
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
		try {
			SVM.svm_save_model(outputFile, model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not save model to file.");
			e.printStackTrace();
		}

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
		Mat results = new Mat(featMat.rows(), 1, CvType.CV_8S); // Must be signed
		for(int row = 0; row < featMat.rows(); row++) {
			if(row == 60) {
				svm_node[] tempNodes = mat2svm_nodeArray(featMat, row);
				double sampleClass = svm_plane_predict(tempNodes);
				results.put(row, 0, sampleClass);
			}
			else {
				svm_node[] tempNodes = mat2svm_nodeArray(featMat, row);
				//System.out.println("tempNode size = " + tempNodes.length);
				double sampleClass = svm_plane_predict(tempNodes);
				//double sampleClass = SVM.svm_predict(model, tempNodes);
				results.put(row, 0, sampleClass);
			}
		}
		// Förra versionen
		//SVM.predict_all(featMat, results);
		
		double[] res = getResult(classes, results,trainReader.getNumOfClasses(),trainReader.getNumOfEachClass());
	}
	
	/*
	 * TODO: How should the result be presented?
	 */
	public static  double[] getResult(Mat classes, Mat results, int numOfClasses,int[] numOfEachClass)
	{
		System.out.println("Results: " + results.t().dump());
		System.out.println("Classes: " + classes.t().dump());
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
		try {
			model = svm.svm_load_model(file);
		} catch (IOException e) {
			System.out.println("Error in HOGIdentification: " + e.getMessage());
		}
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
	
	public double svm_plane_predict(svm_node[] features) {
		double classResult;
		double scalarprodResult = 0;
		scalarprodResult += w.get(0);
		for(int index = 0; index < features.length; index++) {
			scalarprodResult += w.get(index+1)*features[index].value;
		}
		if (scalarprodResult >= 0){
			classResult = 0;
		}
		else {
			classResult = 1;
		}
		return classResult;
	}
	
	public void svm_model2primalValue() {
		w.clear();
		w.add(-model.rho[0]);
		if (model.label[1] == 0) {
			w.set(0, -w.get(0));
		}
		for(int featureIndex = 0; featureIndex < model.SV[0].length; featureIndex++) {
				w.add(0.0);
			for(int svIndex = 0; svIndex < model.sv_coef[0].length; svIndex++) {
				w.set(featureIndex+1, w.get(featureIndex+1) + model.SV[svIndex][featureIndex].value * model.sv_coef[0][svIndex]);
				//System.out.println(featureIndex + "  " +svIndex );
				}
			if (model.label[1] == 0) {
				w.set(featureIndex+1, -w.get(featureIndex+1));
			}
		}
	}
	
	@Override
	public void loadPrimalValueFromFile(String filepath) {
		try {
			w.clear();
			Scanner input = new Scanner(new File(filepath));
			
			while(input.hasNext()) {
				String s = input.next();
				w.add(Double.parseDouble(s));
			}
			input.close();
		}
		catch (Exception e) {
			System.out.println("Error loading file: " + filepath);
		}
	}
	
	public void savePrimalValue2file(String filePath)
	{
		try {
			File file = new File(filePath);
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(int featureIndex = 0; featureIndex < w.size(); featureIndex++)
			{
				bw.write(w.get(featureIndex) + " ");
			}
			bw.close();
 
			System.out.println("Primal Value Saved");
 
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
