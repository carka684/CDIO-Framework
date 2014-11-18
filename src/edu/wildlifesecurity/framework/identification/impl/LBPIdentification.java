package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Mat;

import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;
import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.identification.IdentificationEvent;

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

	@Override
	public ISubscription addEventHandler(EventType type,
			IEventHandler<IdentificationEvent> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void loadPrimalVariableFromFile(String file) {
		// TODO Auto-generated method stub
	}
}
