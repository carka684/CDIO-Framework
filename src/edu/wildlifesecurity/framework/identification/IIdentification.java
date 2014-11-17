package edu.wildlifesecurity.framework.identification;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;

public interface IIdentification extends IComponent {
	
	ISubscription addEventHandler(EventType type, IEventHandler<IdentificationEvent> handler);
	
	void trainClassifier(String pos, String neg, String outputFile);
	
	void evaluateClassifier(String pos, String neg);
	
	IClassificationResult classify(Mat image);
	
	void loadClassifierFromFile(String file);
	
	void loadPrimalVariableFromFile(String file);
}
