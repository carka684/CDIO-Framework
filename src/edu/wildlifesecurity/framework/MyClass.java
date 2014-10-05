package edu.wildlifesecurity.framework;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG;
import org.opencv.video.Video;

import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;

public class MyClass {
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture vc = new VideoCapture("C:\\Users\\Tobias\\Documents\\Datasets\\En_person_inomhus\\scene_%04d.png");
	
		System.out.println("Is opened: " + vc.isOpened());
		
		// Grab & retrieve the next frame
		Mat img = new Mat();
		vc.read(img);
		
		// Create Background Subtractor
		
		Mat fgMask = new Mat();
		BackgroundSubtractorMOG bgs = new BackgroundSubtractorMOG();
		bgs.apply(img, fgMask);

	}
	
}
