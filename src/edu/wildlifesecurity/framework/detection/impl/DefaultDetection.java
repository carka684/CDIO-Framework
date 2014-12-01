package edu.wildlifesecurity.framework.detection.impl;

import java.util.Vector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfFloat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.KalmanFilter;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.EventDispatcher;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.detection.DetectionEvent;
import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.IDetection;

public class DefaultDetection extends AbstractComponent implements IDetection
{
	private EventDispatcher<DetectionEvent> dispatcher = new EventDispatcher<DetectionEvent>();
	
	private BackgroundSubtractorMOG2 bgs;
	private int age;

	@Override
	public void init()
	{
		float varThreshold = Float.parseFloat(configuration.get("Detection_varThreshold").toString());
		Boolean bShadowDetection = Boolean.parseBoolean(configuration.get("Detection_bShadowDetection").toString());
		bgs = new BackgroundSubtractorMOG2(0, varThreshold, bShadowDetection);
		// InitTime = Integer.parseInt(configuration.get("Detection_InitTime").toString());
	}
	
	public ISubscription addEventHandler(EventType type, IEventHandler<DetectionEvent> handler){
		return dispatcher.addEventHandler(type, handler);
	}
	
	private DetectionResult getImagesInsideContours(Vector <MatOfPoint> contours,Mat img, Mat rawDetection, int minAreaOnImage)
	{
		Vector<Detection> detVec = new Vector<>();
		for (int i = 0; i < contours.size(); i++)
		{
			double area = Imgproc.contourArea(contours.get(i));
			if (area > minAreaOnImage)
			{
				Rect boundBox = Imgproc.boundingRect(contours.get(i));
				boundBox = squarify(boundBox, rawDetection.width(), rawDetection.height());
				Mat regionImage = img.submat(boundBox);
				Detection result = new Detection(boundBox,regionImage); 
				detVec.add(result);
			}
		}
		DetectionResult detections = new DetectionResult(detVec,rawDetection, img);
		return detections;
	}
	
	private Rect squarify(Rect rect, int imWidth, int imHeight){
		int side = Math.max(rect.width, rect.height);
		if(side > Math.min(imWidth, imHeight))
			side = Math.min(imWidth, imHeight);
		
		rect.x += rect.width/2 - side/2;
		rect.y += rect.height/2 - side/2;
		
		if(rect.x < 0)
			rect.x = 0;
		if(rect.y < 0)
			rect.y = 0;
		if(rect.x + side > imWidth)
			rect.x = imWidth - side;
		if(rect.y + side > imHeight)
			rect.y = imHeight - side;
		
		rect.width = side;
		rect.height = side;
		
		return rect;
	}
	
	private Mat openAndClose(Mat binaryImage, int numOperationsInOpening, int numOperationsInClosing)
	{
		Mat morphKernel = new Mat();
		Mat modifiedImage = binaryImage.clone();
		morphKernel = Mat.ones(3, 3, CvType.CV_8U);
		
		// Opening
		for(int i = 0; i < numOperationsInOpening; i++)
			Imgproc.erode(modifiedImage, modifiedImage, morphKernel);
		for(int i = 0; i < numOperationsInOpening; i++)	
			Imgproc.dilate(modifiedImage, modifiedImage, morphKernel);
		
		// Closing
		for(int i = 0; i < numOperationsInClosing; i++)
			Imgproc.dilate(modifiedImage, modifiedImage, morphKernel);
		for(int i = 0; i < numOperationsInClosing; i++)
			Imgproc.erode(modifiedImage, modifiedImage, morphKernel);
			
		return modifiedImage;
	}
	
		
	@Override
	public DetectionResult getObjInImage(Mat img)
	{
		age++;
		int InitTime = Integer.parseInt(configuration.get("Detection_InitTime").toString());
		double highLearningRate = Double.parseDouble(configuration.get("Detection_highLearningRate").toString());
		double lowLearningRate = Double.parseDouble(configuration.get("Detection_lowLearningRate").toString());
				
		DetectionResult result;
		Mat fgMask = new Mat();
		if(age < InitTime)	
		{
			bgs.apply(img, fgMask, highLearningRate);
		}
		else
		{
			bgs.apply(img, fgMask, lowLearningRate);
		}
		
		Imgproc.threshold(fgMask, fgMask, 127.0, 255.0, Imgproc.THRESH_TOZERO);
		
		int numOpen = Integer.parseInt(configuration.get("Detection_numOperationsInOpening").toString());
		int numClose = Integer.parseInt(configuration.get("Detection_numOperationsInClosing").toString());		
		Mat fgMaskMod = openAndClose(fgMask, numOpen, numClose);
		
		Vector <MatOfPoint> contours = new Vector <MatOfPoint>();
		Mat contourHierarchy = new Mat();
	
		Imgproc.findContours(fgMaskMod, contours, contourHierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		
		int MinimalSizeOfObjets = Integer.parseInt(configuration.get("Detection_MinSizeOfDetectedObjects").toString());
		result = getImagesInsideContours(contours,img,fgMask, MinimalSizeOfObjets);
		//result.rawDetection = fgMask;
		
		// Dispatch event
		dispatcher.dispatch(new DetectionEvent(DetectionEvent.NEW_DETECTION, result));
		
		return result;
	}
}