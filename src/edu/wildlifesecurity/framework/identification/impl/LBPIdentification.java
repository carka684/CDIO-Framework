package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Mat;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;
import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;

public class LBPIdentification extends AbstractComponent implements IIdentification {

	@Override
	public IClassificationResult classify(Mat features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trainClassifier(String pos, String neg, String outputFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluateClassifier(String pos, String neg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadClassifierFromFile(String file) {
		// TODO Auto-generated method stub
		
	}

}
