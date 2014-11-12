package edu.wildlifesecurity.framework.tracking.impl;

import java.util.Iterator;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import com.atul.JavaOpenCV.Imshow;

import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;

public class KalmanTracking {
	static Vector<KalmanFilter> kalVec;
	static int nextID;
	static int errorDist;
	static double errorHeight;
	static double errorWidth;
	static int numOfUnseen;
	static double maxAreaIncrease;

	public static void init()
	{
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
		errorDist = 80; // Read from config!!
		errorHeight = 0.8;
		errorWidth = 0.8;
		numOfUnseen = 25; // -- || --
		maxAreaIncrease = 0.3;
		
	}
	public void trackRegions(DetectionResult detections,Mat img) throws Exception
	{
		for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
		{
			KalmanFilter kf = iterator.next();
			kf.predict(); 
			kf.addUnseen();
			double[][] pos = kf.getPos();
			Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, kf.getColor(),5);	
			if(kf.getNumOfUnseen() > numOfUnseen)
			{
				iterator.remove();
				System.out.println(kf.getId() + " was removed");
			}
		}				
		for(Detection result : detections.getVector())
		{
			int height = result.getRegion().height;
			int width = result.getRegion().width;
			int x = result.getRegion().x + width/2;
			int y = result.getRegion().y + height/2;
			if(kalVec.isEmpty())
			{
				kalVec.add(new KalmanFilter(nextID,x,y,height,width));
				nextID++;
			}	
			else
			{
				double minError = errorDist;
				double minErrorHeight = errorHeight;
				double minErrorWidth = errorWidth;
				KalmanFilter bestKalman = null;
				for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
				{
					KalmanFilter kf = iterator.next();
					double errorDist = kf.getError(x,y);
					double[] errorDims = kf.getErrorDim(height, width);
					//System.out.println("errorArea: " + errorArea);
					if (errorDist < minError && minErrorHeight < errorDims[0] && minErrorWidth < errorDims[1]) // test errorArea here.
					{
						minError = errorDist;
						minErrorHeight = errorDims[0];
						minErrorWidth = errorDims[1];
						bestKalman = kf;
					}   
					
				}
				if(bestKalman != null)
				{
					result.setID(bestKalman.getId());
					result.setColor(bestKalman.getColor());
					bestKalman.seen();
					bestKalman.correct(x,y,height,width);
				}
				else
				{
					KalmanFilter kf = new KalmanFilter(nextID,x,y,height,width);
					result.setID(kf.getId());
					result.setColor(kf.getColor());
					kalVec.add(kf);								
					
					nextID++;
					break;
				}
			}
		}
	}

}
