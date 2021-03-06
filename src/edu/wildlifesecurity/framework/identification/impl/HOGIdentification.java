package edu.wildlifesecurity.framework.identification.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.identification.IdentificationEvent;


public class HOGIdentification extends AbstractComponent implements IIdentification {

	/**
	 * Extracts HOG features
	 *
	 */
	private Size imageSize;
	private HOGDescriptor hog;
	private svm SVM;
	private svm_model model;
	private svm_parameter params;
	private HashMap<Integer,Vector<Double>> mapOfvectors = new HashMap<Integer,Vector<Double>>();
	private Vector<Double> wHumanOther = new Vector<Double>(); // Primal variable
	private Vector<Double> wRhinoOther = new Vector<Double>(); // Primal variable
	private Vector<Double> wRhinoHuman = new Vector<Double>(); // Primal variable
	private Vector<Double> w = new Vector<Double>(); // Primal variable
	private int numberOfClasses;
	private EventDispatcher<IdentificationEvent> dispatcher =  new EventDispatcher<IdentificationEvent>();

	@Override
	public ISubscription addEventHandler(EventType type, IEventHandler<IdentificationEvent> handler){
		return dispatcher.addEventHandler(type, handler);
	}

	@Override
	public void init(){
		// TODO: Should be loaded from configuration
		// HOG stuff
		int imageSide = Integer.parseInt(configuration.get("Identification_imageSide").toString()); // From config
		imageSize = new Size(imageSide, imageSide);
		int blockSide = Integer.parseInt(configuration.get("Identification_hog_blockSide").toString());
		int blockStrideSide = Integer.parseInt(configuration.get("Identification_hog_blockStrideSide").toString());
		int cellSide = Integer.parseInt(configuration.get("Identification_hog_cellSide").toString());
		int nbins = Integer.parseInt(configuration.get("Identification_hog_numberOfBins").toString());
		hog = new HOGDescriptor(imageSize,new Size(blockSide,blockSide),new Size(blockStrideSide,blockStrideSide),new Size(cellSide,cellSide),nbins);
		
		// SVM stuff
		SVM = new svm();
		model = new svm_model();
		params = new svm_parameter();
		params.kernel_type = Integer.parseInt(configuration.get("Identification_libsvm_kernelType").toString());
		params.C = Integer.parseInt(configuration.get("Identification_libsvm_C").toString());
		params.eps = Double.parseDouble(configuration.get("Identification_libsvm_eps").toString());

		numberOfClasses = Integer.parseInt(configuration.get("Identification_numberOfClasses").toString());
	    
		// Load classifier if a configuration option exists
		for(int i = 0; i < numberOfClasses; i++)
		{
		if(configuration != null && configuration.containsKey("Identification_Classifier" + i))
			loadPrimalVariableFromFile((configuration.get("Identification_Classifier" + i).toString()), i);
		}
	}

	public Mat extractFeatures(Mat inputImage) { 
		MatOfFloat features = new MatOfFloat();
		Imgproc.resize(inputImage, inputImage, imageSize);
		hog.compute(inputImage, features);
		return features;
	}

	@Override
	public Classes classify(Mat image) {
		Mat features = extractFeatures(image);
		svm_node[] imageFeatureNodes = featureMat2svm_nodeArray(features.t(), 0); // features must be a row-vector
		Classes resClass = svmPlanePredict(imageFeatureNodes); // classify using the loaded planes
		ClassificationResult result = new ClassificationResult(resClass, image);
		dispatcher.dispatch(new IdentificationEvent(IdentificationEvent.NEW_IDENTIFICATION, result));
		return resClass;
	}
	
	//////////////////FOR CLASSIFYING WITH 3 PLANES/////////////////////
	public Classes svmPlanePredict(svm_node[] features) {
		// Get w from hashMap mapOfVectors 
		Classes classResult = Classes.UNIDENTIFIED;
		double dotProductResult;
		for(Classes c : Classes.values()) {
			w = mapOfvectors.get(c.ordinal());
			dotProductResult = dotProductBiasedWeight(features, w); // Compute which side of the plane the sample is located
			if (dotProductResult >= 0) {
				classResult = c;
				// Also check for the Rhino vs Human plane
				w = mapOfvectors.get(2); // Get RhinoHuman plane
				dotProductResult = dotProductBiasedWeight(features, w); // Compute which side of the plane the sample is located
				if (classResult == Classes.RHINO && dotProductResult <= 0 )
					classResult = Classes.HUMAN; // Changed from Rhino to Human by the RhinoHuman plane
				else if(classResult == Classes.HUMAN && dotProductResult >= 0)
					classResult = Classes.RHINO; // Changed from Human to Rhino by the RhinoHuman plane
				return classResult;
			}
		}	
		return classResult;
	}
	
	public double dotProductBiasedWeight(svm_node[] features, Vector<Double> w)
	{
		double result = 0;
		result += w.get(0); // biased weight
		for(int index = 0; index < features.length; index++) {
			result += w.get(index+1)*features[index].value;
		}
		return result;
	}
	
	// Evaluate the result of the combined 3-class classifier
	@Override
	public void evaluateClassifier(String valFolder)
	{
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(valFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat results = new Mat(trainFiles.size(), 1, CvType.CV_8S); // Must be signed

		int counter = 0;
		for(String file : trainFiles)
		{
			Mat img = new Mat();
			try {
				img = Highgui.imread(file,Highgui.CV_LOAD_IMAGE_GRAYSCALE);
			} catch (Exception e) {
				System.out.println("Error loading: " + file);
				e.printStackTrace();
			}
			Integer sampleClass = classify(img).ordinal();
			results.put(counter, 0, sampleClass);
			counter++;
			img.release();
		}
		double[] res = getResult(classes, results, trainReader.getNumOfClasses(), trainReader.getNumOfEachClass());
	}
	
//////////////////FOR CLASSIFYING WITH PLANE/////////////////////
	@Override
	public void loadPrimalVariableFromFile(String filepath, int wNum) {	
		try {
			Scanner input = new Scanner(new File(filepath));
			String str = new String();
			while(input.hasNext()) {
				str = input.next();
				if(wNum == 0)
					wRhinoOther.add(Double.parseDouble(str));
				else if(wNum == 1)
					wHumanOther.add(Double.parseDouble(str));
				else
					wRhinoHuman.add(Double.parseDouble(str));
			}
			input.close();
			System.out.println("Loaded classifier!");
		}
		catch (Exception e) {
			System.out.println("Error loading file: " + filepath);
		}
		
		if(wNum == 0)
			mapOfvectors.put(wNum, wRhinoOther);
		else if(wNum == 1)
			mapOfvectors.put(wNum, wHumanOther);
		else
			mapOfvectors.put(wNum, wRhinoHuman);
	}
	
////////////////// FOR TRAINING /////////////////////
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
				System.out.println("Error loading: " + file);
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

//////////////////FOR TRAINING /////////////////////
	@Override
	public void trainClassifier(String trainFolder, String outputFile) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(trainFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat featureMat = extractFeaturesFromFiles(trainFiles);
		svm_problem featureProblem = featureMat2svm_problem(featureMat, classes);	
		model = svm.svm_train(featureProblem, params);
			svm_model2primalVariable();
		try {
			savePrimalVariable2file(outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not save model to file.");
			e.printStackTrace();
		}
	}
	
//////////////////FOR EVALUTATION OF TRAINING////////////////////
	// Evaluates a classifier with one plane after it has been trained
	public void evaluateTrainedClassifier(String valFolder) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(valFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat featureMat = extractFeaturesFromFiles(trainFiles);
		Mat results = new Mat(featureMat.rows(), 1, CvType.CV_8S); // Must be signed
		for(int row = 0; row < featureMat.rows(); row++) {
				svm_node[] featureNodes = featureMat2svm_nodeArray(featureMat, row);
				double sampleClass = svm.svm_predict(model, featureNodes);
				results.put(row, 0, sampleClass);
		}
		double[] res = getResult(classes, results,trainReader.getNumOfClasses(),trainReader.getNumOfEachClass());
	}
	
//////////////////FOR EVALUTATION OF TRAINING/////////////////////
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
	
//////////////////FOR TRAINING /////////////////////
	public void svm_model2primalVariable() {
		w.clear();
		w.add(-model.rho[0]);
		if (model.label[1] == 0) {
			w.set(0, -w.get(0));
		}
		for(int featureIndex = 0; featureIndex < model.SV[0].length; featureIndex++) {
			w.add(0.0);
			for(int svIndex = 0; svIndex < model.sv_coef[0].length; svIndex++) {
				w.set(featureIndex+1, w.get(featureIndex+1) + model.SV[svIndex][featureIndex].value * model.sv_coef[0][svIndex]);
			}
			if (model.label[1] == 0) {
				w.set(featureIndex+1, -w.get(featureIndex+1));
			}
		}
	}

//////////////////FOR TRAINING TO SAVE PLANE /////////////////////
	public void savePrimalVariable2file(String filePath) throws IOException
	{
		try {
			File file = new File(filePath);
			// if file does not exists, then create it
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
			System.out.println("Primal variable Saved");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/////////// CONVERT FEATURES TO LIBSVM COMPATIBILITY ////////////////////// 	
	// Convert from featureMatrix to svm_problem, which is required as input to svm_train
	public svm_problem featureMat2svm_problem(Mat featureMat, Mat classes) {
		svm_problem result = new svm_problem();
		result.l = featureMat.rows(); // number of samples
		result.y = new double[featureMat.rows()]; // correct class
		svm_node[][] svmNodes = new svm_node[featureMat.rows()][featureMat.cols()]; // nodes containing index and features
		for (int row = 0; row < featureMat.rows(); row++)  {
			result.y[row] = classes.get(row, 0)[0];
			for(int col = 0; col < featureMat.cols(); col++) {
				svm_node featureNode = new svm_node();
				if (col != featureMat.cols() - 1) {
					featureNode.index = col;
				}
				else {
					featureNode.index = -1;
				}
				featureNode.value = featureMat.get(row, col)[0];
				svmNodes[row][col] = featureNode;
			}
		}
		result.x = svmNodes;
		return result;
	}
	
	// Convert from feature-vector to svm_node[], which is required as input to svmPlanePredict
	public svm_node[] featureMat2svm_nodeArray(Mat featureMat, int sampleNr) {
		svm_node[] result = new svm_node[featureMat.cols()];
		for(int col = 0; col < featureMat.cols(); col++) {
			svm_node featureNode = new svm_node();
			if (col != featureMat.cols() - 1) {
				featureNode.index = col;
			}
			else {
				featureNode.index = -1;
			}
			featureNode.value = featureMat.get(sampleNr, col)[0];
			result[col] = featureNode; 
		}

		return result;
	}
}
