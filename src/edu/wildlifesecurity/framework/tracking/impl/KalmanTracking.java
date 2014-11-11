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
	static double maxAreaIncrease;

	public static void init()
	{
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
		errorDist = 80; // Read from config!!
		numOfUnseen = 15; // -- || --
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
			Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, new Scalar(255-kf.getId()*50, kf.getId()*60+80,0),5);	
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
				KalmanFilter bestKalman = null;
				for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
				{
					KalmanFilter kf = iterator.next();
					double errorDist = kf.getError(x,y);
					double errorArea = kf.getErrorArea(height*width);
					System.out.println(errorArea);
					if (errorDist < minError)
					{
						minError = errorDist;
						bestKalman = kf;
					}   
				}
				if(bestKalman != null)
				{
					result.setID(bestKalman.getId());
					result.setColor(bestKalman.getColor());
					bestKalman.seen();
					bestKalman.correct(x,y,height,width);
					System.out.println(bestKalman.getKalman().getState_post().transpose());
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
