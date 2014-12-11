package edu.wildlifesecurity.framework.identification.impl;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.identification.IdentificationEvent;

public class LBPIdentification extends AbstractComponent implements IIdentification {

	@Override
	public Classes classify(Mat features) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void trainClassifier(String trainFolder, String outputFile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluateClassifier(String valFolder) {
		// TODO Auto-generated method stub
		
	}

	/*@Override
	public void loadClassifierFromFile(String file) {
		// TODO Auto-generated method stub
		
	}*/

	@Override
	public ISubscription addEventHandler(EventType type,
			IEventHandler<IdentificationEvent> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void loadPrimalVariableFromFile(String file , int wNum) {
		// TODO Auto-generated method stub
	}
}
