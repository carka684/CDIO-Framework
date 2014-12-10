package edu.wildlifesecurity.framework.identification;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;

public interface IIdentification extends IComponent {
	
	/**
	 * Enables subscription of IdentificationEvent.
	 */
	ISubscription addEventHandler(EventType type, IEventHandler<IdentificationEvent> handler);
	
	/**
	 * Classifies an image using the pretrained classifier and returns the class as
	 * one of the enums defined in Classes.
	 */
	Classes classify(Mat image);
	
	/**
	 * Trains a classifying plane for two classes, which samples are located in two different
	 * folders located in the folder with path trainFolder. The primal variable (classifying plane)
	 * is saved in a text file named outputFile.
	 */
	void trainClassifier(String trainFolder, String outputFile);
	
	/**
	 * Evaluates the performance of the classifier that is combined by several primal
	 * variables (classifying planes).
	 */
	void evaluateClassifier(String valFolder);

	/**
	 * Loads a primal variable (classifying plane) from file and stores it in a HashMap
	 * on position wNum.
	 */
	void loadPrimalVariableFromFile(String file, int wNum);
}
