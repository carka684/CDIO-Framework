package edu.wildlifesecurity.framework.tracking.impl;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import com.sixlegs.png.PngImage;

import edu.wildlifesecurity.framework.IImageEncoder;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.tracking.Capture;

@SuppressWarnings("serial")
public class SerializableCapture implements Externalizable {

	public static IImageEncoder encoder;
	
	private Capture capture;
	
	public SerializableCapture(){ }
	
	public SerializableCapture(Capture capture){
		this.capture = capture;
	}
	
	public Capture getCapture()
	{
		return capture;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(capture.id);
		out.writeInt(capture.trapDeviceId);
		out.writeObject(capture.classification);
		out.writeObject(capture.timeStamp);
		out.writeUTF(capture.GPSPos);

		// Encode image Mat to png 
		/*int type = BufferedImage.TYPE_3BYTE_BGR;
		int blen = capture.regionImage.channels()*capture.regionImage.rows()*capture.regionImage.cols();
		byte[] b = new byte[blen];
		capture.regionImage.get(0,0,b);
		BufferedImage img = new BufferedImage(capture.regionImage.cols(), capture.regionImage.rows(), type);
		img.getRaster().setDataElements(0, 0, capture.regionImage.cols(), capture.regionImage.rows(), b);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PngEncoder encoder = new PngEncoder();
		encoder.setCompression(0);
		encoder.encode(img, baos);*/
		
		byte[] array = encoder.encode(capture.regionImage);
		System.out.println("Serialized image length: " + array.length);
		out.writeInt(array.length); // Write size first
		out.write(array);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		capture = new Capture();
		capture.id = in.readInt();
		capture.trapDeviceId = in.readInt();
		capture.classification = (Classes) in.readObject();
		capture.timeStamp = (Date) in.readObject();
		capture.GPSPos = in.readUTF();
		
		// Read image
		int size = in.readInt();
		byte[] array = new byte[size];
		in.readFully(array, 0, size);
		
		PngImage decoder = new PngImage();
		BufferedImage img = decoder.read(new ByteArrayInputStream(array), true);
		Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, ((DataBufferByte)img.getRaster().getDataBuffer()).getData());
		
		capture.regionImage = mat;
	}
	
}
