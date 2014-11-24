package edu.wildlifesecurity.framework;

import org.opencv.core.Mat;

public interface IImageDecoder {
	
	Mat decode(byte[] img);
	
}
