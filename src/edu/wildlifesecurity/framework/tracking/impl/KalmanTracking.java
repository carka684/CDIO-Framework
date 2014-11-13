package edu.wildlifesecurity.framework.tracking.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.EventDispatcher;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.tracking.Capture;
import edu.wildlifesecurity.framework.tracking.ITracking;
import edu.wildlifesecurity.framework.tracking.TrackingEvent;

public class KalmanTracking extends AbstractComponent implements ITracking {
	private EventDispatcher<TrackingEvent> dispatcher = new EventDispatcher<TrackingEvent>();
	
	private Vector<KalmanFilter> kalVec;
	private int nextID;
	private int errorDist;
	private double errorHeight;
	private double errorWidth;
	private int numOfUnseen;
	private double correctClassRatio;
	private int numOfSeen;
	
	public void init()
	{
//		variable = configuration.get("");
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
		errorDist = 80; // Read from config!!
		errorHeight = 0.7;
		errorWidth = 0.7;
		numOfUnseen = 25; 
		correctClassRatio = 0.9;
		numOfSeen = 10;
	}
	public void trackRegions(DetectionResult detections,Mat img) throws Exception
	{
		for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
		{
			KalmanFilter kf = iterator.next();
			kf.predict(); 
			kf.addUnseen();
			double[][] pos = kf.getPos();
			Core.circle(img, new Point(pos[0][0],pos[1][0]), 5, kf.getColorKalman(),5);	
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
			int classification = result.getClassification();
			if(kalVec.isEmpty())
			{
				kalVec.add(new KalmanFilter(nextID++,x,y,height,width));
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
					result.setColor(bestKalman.getColorRegion()); 
					bestKalman.seen();
					bestKalman.correct(x,y,height,width);
					bestKalman.addClass(classification);
					sendEvent(bestKalman,result.getRegionImage(), TrackingEvent.NEW_TRACK);
					if(bestKalman.isDone(numOfSeen,correctClassRatio))
					{
						sendEvent(bestKalman,result.getRegionImage(), TrackingEvent.NEW_CAPTURE);
					}
				}
				else
				{
					KalmanFilter kf = new KalmanFilter(nextID++,x,y,height,width);
					result.setID(kf.getId());
					result.setColor(kf.getColorRegion());
					kf.addClass(classification);
					kalVec.add(kf);								
					break;
				}
			}
		}
	}
	@Override
	public ISubscription addEventHandler(EventType type, IEventHandler<TrackingEvent> handler) {
		return dispatcher.addEventHandler(type, handler);
	}
	public void sendEvent(KalmanFilter kf,Mat img, EventType type)
	{
		Capture capture = new Capture();
		capture.captureId = kf.getId();
		capture.position = "" + kf.getPos()[0][0] + " " + kf.getPos()[1][0];
		capture.timeStamp = new Timestamp(new Date().getTime());
		capture.image = img;
		capture.trapDeviceId = -1;
		dispatcher.dispatch(new TrackingEvent(type, capture));
		System.out.println(capture.timeStamp + " sent event");
	}

}
