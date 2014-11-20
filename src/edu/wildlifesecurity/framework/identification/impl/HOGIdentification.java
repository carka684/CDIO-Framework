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
	HOGDescriptor hog;
	svm SVM;
	svm_model model;
	svm_parameter params;
	Size s;
	HashMap<Integer,Vector<Double>> mapOfvectors = new HashMap<Integer,Vector<Double>>();
	Vector<Double> wHumanOther = new Vector<Double>(); // Primal variable
	Vector<Double> wRhinoOther = new Vector<Double>(); // Primal variable
	Vector<Double> wRhinoHuman = new Vector<Double>(); // Primal variable
	Vector<Double> w = new Vector<Double>(); // Primal variable
	//int numOfClasses;
	int numberOfClasses;
	Vector<Double> v = new Vector<Double>();
	private EventDispatcher<IdentificationEvent> dispatcher =  new EventDispatcher<IdentificationEvent>();

	@Override
	public ISubscription addEventHandler(EventType type, IEventHandler<IdentificationEvent> handler){
		return dispatcher.addEventHandler(type, handler);
	}

	@Override
	public void init(){
		// TODO: Should be loaded from configuration
		s = new Size(240, 240);
		hog = new HOGDescriptor(s,new Size(16,16),new Size(8,8),new Size(8,8),9);
		
		SVM = new svm();
		model = new svm_model();
		params = new svm_parameter();
		int linearSVM = 0;
		params.kernel_type = linearSVM;
		params.C = 16;
		params.eps = 0.01;
		numberOfClasses = 2;
	    
		// Load classifier if a configuration option exists
		
		for(int i = 0; i < numberOfClasses; i++)
		{
		if(configuration != null && configuration.containsKey("Identification_Classifier"))
			loadPrimalVariableFromFile((configuration.get("Identification_Classifier").toString()), i);
		}
	}

	public Mat extractFeatures(Mat inputImage) { 
		MatOfFloat features = new MatOfFloat();
		Imgproc.resize(inputImage, inputImage, s);
		hog.compute(inputImage, features);
		return features;
	}

	@Override
	public Classes classify(Mat image) {
		Mat features = extractFeatures(image);
		
		svm_node[] imageFeatureNodes = featureMat2svm_nodeArray(features.t(), 0); // features must be a row-vector
		double res = svmPlanePredict(imageFeatureNodes); // classify using the plane
		
		Classes resClass;
		if(res == 1)
			 resClass = Classes.HUMAN;
		else if(res == 0)
			resClass = Classes.RHINO;
		else
			resClass = Classes.UNIDENTIFIED;
		v.add(res);
		ClassificationResult result = new ClassificationResult(resClass, image);
		dispatcher.dispatch(new IdentificationEvent(IdentificationEvent.NEW_IDENTIFICATION, result));
		return resClass;
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
	public void trainClassifier(String trainFolder,String UNUSED, String outputFile) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(trainFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat featureMat = extractFeaturesFromFiles(trainFiles);
		svm_problem featureProblem = featureMat2svm_problem(featureMat, classes);	
		model = SVM.svm_train(featureProblem, params);
			svm_model2primalVariable();
		try {
			savePrimalVariable2file(outputFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not save model to file.");
			e.printStackTrace();
		}
	}
	
//////////////////FOR TRAINING EVALUTATION/////////////////////
	@Override
	public void evaluateClassifier(String valFolder, String UNUSED) {
		ImageReader trainReader = new ImageReader();
		trainReader.readImages(valFolder);
		Vector<String> trainFiles = trainReader.getFilesVec();
		Mat classes = trainReader.getClasses();
		Mat featureMat = extractFeaturesFromFiles(trainFiles);
		Mat results = new Mat(featureMat.rows(), 1, CvType.CV_8S); // Must be signed
		for(int row = 0; row < featureMat.rows(); row++) {
				svm_node[] featureNodes = featureMat2svm_nodeArray(featureMat, row);
				double sampleClass = svmPlanePredict(featureNodes);
				results.put(row, 0, sampleClass);
		}
		double[] res = getResult(classes, results,trainReader.getNumOfClasses(),trainReader.getNumOfEachClass());
	}
	
	/*
	 * TODO: How should the result be presented?
	 */
//////////////////FOR TRAINING EVALUATION/////////////////////
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
	/*
	@Override
	public void loadClassifierFromFile(String file) {
		try {
			model = svm.svm_load_model(file);
		} catch (IOException e) {
			System.out.println("Error in HOGIdentification: " + e.getMessage());
		}
	}
	*/
	
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
//////////////////FOR CLASSIFYING WITH PLANE/////////////////////
	public double svmPlanePredict(svm_node[] features) {
		//get w from hashMap mapOfVectors and the classresult is the key in map
		
		double classResult = numberOfClasses;
		double scalarprodResult = 0;
		for(int i = 0; i < numberOfClasses; i++)
		{
			w = mapOfvectors.get(i);
			scalarprodResult += w.get(0); // biased weight
			for(int index = 0; index < features.length; index++) {
				scalarprodResult += w.get(index+1)*features[index].value;
			}
			
			if (scalarprodResult >= 0) {
				classResult = i;
				if(classResult == 0 || classResult == 1) //Rhino or Human, check with RhinoHuman plane
				{
					scalarprodResult = 0;
					w = mapOfvectors.get(2); //get RhinoHuman plane
					scalarprodResult += w.get(0); // biased weight
					for(int index = 0; index < features.length; index++) {
						scalarprodResult += w.get(index+1)*features[index].value;
					}
					if (classResult == 0 && scalarprodResult <= 0 )
						classResult = 1.0; //Changed from Rhino to Human by the RhinoHuman plane
					else if(classResult == 1 && scalarprodResult >= 0)
						classResult = 0.0; //Changed from Human to Rhino by the RhinoHuman plane					
				}
				return classResult;
			}
			scalarprodResult = 0;
		}	
		return classResult;
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
	
//////////////////FOR CLASSIFYING WITH PLANE/////////////////////
	@Override
	public void loadPrimalVariableFromFile(String filepath, int classNum) {
		
			try {
				//w.clear();
				Scanner input = new Scanner(new File(filepath));
				String str = new String();
				while(input.hasNext()) {
					str = input.next();
					if(classNum == 0)
						wRhinoOther.add(Double.parseDouble(str));
					
					else if(classNum == 1)
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
			if(classNum == 0)
				mapOfvectors.put(classNum, wRhinoOther);
			
			else if(classNum == 1)
				mapOfvectors.put(classNum, wHumanOther);
			else
				mapOfvectors.put(classNum, wRhinoHuman);
				
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
}
