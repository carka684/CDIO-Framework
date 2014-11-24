package edu.wildlifesecurity.framework;

import org.opencv.core.Mat;

public interface IImageEncoder {

	byte[] encode(Mat image);
	
}
