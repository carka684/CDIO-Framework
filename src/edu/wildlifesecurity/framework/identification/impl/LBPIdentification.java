package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Mat;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;

public class LBPIdentification implements IIdentification {

	@Override
	public Mat extractFeatures(Mat inputImage) {
		ImageProcessor imgProc = new ColorProcessor(10,10);
		LocalBinaryPatterns descriptor = new LocalBinaryPatterns();
		descriptor.run(imgProc);
		
		return null;
	}

	@Override
	public IClassificationResult classify(Mat features) {
		// TODO Auto-generated method stub
		return null;
	}

}
