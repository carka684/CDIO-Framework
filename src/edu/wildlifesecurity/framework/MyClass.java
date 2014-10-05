package edu.wildlifesecurity.framework;
import java.util.List;

import javax.swing.JFrame;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.opencv.video.Video;

import de.lmu.ifi.dbs.jfeaturelib.features.LocalBinaryPatterns;


public class MyClass {
	public static void main(String[] args){
		/*System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		VideoCapture vc = new VideoCapture("C:\\Users\\Tobias\\Documents\\Datasets\\En_person_inomhus\\scene_%04d.png");
	
		System.out.println("Is opened: " + vc.isOpened());
		
		// Grab & retrieve the next frame
		Mat frame = new Mat();
		vc.read(frame);*/
		
		//1. Create the frame.
		JFrame frame = new JFrame("FrameDemo");

		//2. Optional: What happens when the frame closes?
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//3. Create components and put them in the frame.
		//...create emptyLabel...
		//frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);

		//4. Size the frame.
		frame.pack();

		//5. Show it.
		frame.setVisible(true);

	}
}
