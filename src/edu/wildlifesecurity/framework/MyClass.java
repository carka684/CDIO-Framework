package edu.wildlifesecurity.framework;
import org.opencv.core.Core;
import org.opencv.core.Mat;


public class MyClass {
	public static void main(String[] args){
		System.loadLibrary("opencv_java248");
		Mat mat = new Mat(1);
		System.out.println("Heeej");
	}
}
