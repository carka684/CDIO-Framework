package edu.wildlifesecurity.framework;
import java.util.List;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;


public class MyClass {
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		ImagePlus imgProc = new Opener().openURL("http://rsb.info.nih.gov/ij/images/Dot_Blot.jpg");
		LocalBinaryPatterns descriptor = new LocalBinaryPatterns();
		descriptor.setNumberOfHistogramBins(10);
		descriptor.run(imgProc.getProcessor());
		
		List<double[]> features = descriptor.getFeatures();
		
		System.out.println("Heeeeej");
	}
}
