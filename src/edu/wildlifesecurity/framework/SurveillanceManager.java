package edu.wildlifesecurity.framework;

import org.opencv.core.Core;

public abstract class SurveillanceManager {

	protected SurveillanceManager(){
		
		// Initialize OpenCV
		if ((System.getProperty("os.arch").equals("amd64") 
				&& System.getProperty("os.name").equals("Linux"))) // if 64 bit Linux
		{
			System.loadLibrary("opencv_java249_x64"); // use 64 bit linux openCV library (Android uses 32bit)
			
		}
		else // else
		{
			System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); // use normal openCV library
		}
		
		
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
