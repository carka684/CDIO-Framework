package edu.wildlifesecurity.framework;
import org.opencv.core.Core;
import org.opencv.core.Mat;


public class MyClass {
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = new Mat(1);
		System.out.println("Heeeeej");
	}
}
