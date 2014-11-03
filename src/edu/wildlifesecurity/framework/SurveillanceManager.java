package edu.wildlifesecurity.framework;

import org.opencv.core.Core;

public abstract class SurveillanceManager {

	protected SurveillanceManager(){
		
		// Initialize OpenCV
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		
	}
	
	/**
	 * Abstract method to start the manager operation
	 * 
	 */
	abstract public void start();
	
	/**
	 * Abstract method to stop the manager operation
	 */
	abstract public void stop();
	
}
