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
	static int numOfUnseen;

	public static void init()
	{
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
		errorDist = 80; // Read from config!!
		numOfUnseen = 15; // -- || --
	}
	public void trackRegions(DetectionResult detections,Mat img, Imshow show) throws Exception
	{
		for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
		{
			KalmanFilter kf = iterator.next();
			kf.predict(); 
			kf.addUnseen();
			double[][] pos = kf.getPos();
			Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,0),5);	

			if(kf.getNumOfUnseen() > numOfUnseen)
			{
				iterator.remove();
				System.out.println(kf.getId() + " was removed");
			}
		}				
		for(Detection result : detections.getVector())
		{
			int x = result.getRegion().x + result.getRegion().width/2;
			int y = result.getRegion().y + result.getRegion().height/2;
			if(kalVec.isEmpty())
			{
				kalVec.add(new KalmanFilter(nextID,x,y));
				//setRegionID = nextID++;
				nextID++;
			}	
			else
			{
				double minError = errorDist;
				KalmanFilter bestKalman = null;
				for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
				{
					KalmanFilter kf = iterator.next();
					double error = kf.getError(x,y);
					if (error < minError)
					{
						minError = error;
						bestKalman = kf;
					}   
				}
				if(bestKalman != null)
				{
					bestKalman.seen();
					bestKalman.correct(x, y);
				}
				else
				{
					kalVec.add(new KalmanFilter(nextID,x,y));								
					//setRegionID = nextID;
					nextID++;
					break;
				}
			}
		}
		show.showImage(img);
	}

}
