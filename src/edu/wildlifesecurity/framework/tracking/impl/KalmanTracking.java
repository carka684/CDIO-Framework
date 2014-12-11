package edu.wildlifesecurity.framework.tracking.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.EventDispatcher;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.SensorData;
import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.identification.Classes;
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
		nextID = 0;
		kalVec =  new Vector<KalmanFilter>();
		errorDist = Integer.parseInt(configuration.get("Tracking_max_predict_pos_error").toString()); 
		errorHeight = Double.parseDouble(configuration.get("Tracking_max_predict_height_error").toString());
		errorWidth = Double.parseDouble(configuration.get("Tracking_max_predict_width_error").toString());
		numOfUnseen = Integer.parseInt(configuration.get("Tracking_num_of_missing_frames").toString());
		correctClassRatio = Double.parseDouble(configuration.get("Tracking_ratio_of_same_classification").toString());
		numOfSeen = Integer.parseInt(configuration.get("Tracking_num_of_seen_frames").toString());
	}
	public void trackRegions(DetectionResult detections)
	{
		for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
		{
			KalmanFilter kf = iterator.next();
			kf.predict(); 
			
			kf.addUnseen();
			/*
			 * If the filter hasn't got connected to a detection in numOfUnseen frames
			 * it's removed.
			 */
			if(kf.getNumOfUnseen() > numOfUnseen)
			{
				iterator.remove();
			}
			
		}				
		for(Detection detection : detections.getVector())
		{
			int height = detection.getRegion().height;
			int width = detection.getRegion().width;
			int x = detection.getRegion().x + width/2;
			int y = detection.getRegion().y + height/2;
			Classes classification = detection.getClassification();
			if(kalVec.isEmpty())
			{
				kalVec.add(new KalmanFilter(nextID++,x,y,height,width));
			}	
			else
			{
				/*
				 * If a Kalman-filter which is "good enough" for the current detection
				 * that filter will be bestKalman and gets conntected to the detection. 
				 */
				double minError = errorDist;
				double minErrorHeight = errorHeight;
				double minErrorWidth = errorWidth;
				KalmanFilter bestKalman = null;
				for(Iterator<KalmanFilter> iterator = kalVec.iterator(); iterator.hasNext(); )
				{
					KalmanFilter kf = iterator.next();
					double errorDist = kf.getError(x,y);
					double[] errorDims = kf.getErrorDim(height, width);
					if (errorDist < minError && minErrorHeight < errorDims[0] && minErrorWidth < errorDims[1]) 
					{
						minError = errorDist;
						minErrorHeight = errorDims[0];
						minErrorWidth = errorDims[1];
						bestKalman = kf;
					}   
				}
				/*
				 * If a existing filter was a match that filter (bestKalman) get connected
				 * to the detection and a NEW_TRACK-event is sent.
				 * If bestKalman fulfills the requirements to be a capture a NEW_CAPTURE will be sent. 
				 */
				if(bestKalman != null)
				{
					detection.setID(bestKalman.getId());
					bestKalman.seen();
					bestKalman.correct(x,y,height,width);
					bestKalman.addClass(classification);
					sendEvent(bestKalman, detection, TrackingEvent.NEW_TRACK);
					//System.out.println("Kalman id: " + bestKalman.getId());
					if(bestKalman.isDone(numOfSeen,correctClassRatio) && !bestKalman.isSent() && bestKalman.maxClass != Classes.UNIDENTIFIED)
					{
						System.out.println("Sent NEW_CAPTURE");
						bestKalman.setSent();
						sendEvent(bestKalman,detection, TrackingEvent.NEW_CAPTURE);
					}
				}
				/*
				 * If there wasn't any KF which matched the detection a new filter 
				 * will be created and connected to the detection
				 */
				else
				{
					KalmanFilter kf = new KalmanFilter(nextID++,x,y,height,width);
					detection.setID(kf.getId());
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
	public void sendEvent(KalmanFilter kf,Detection detection, EventType type)
	{
		Capture capture = new Capture(new Date(),detection.getRegionImage(), detection.getClassification(),
				SensorData.latitude,SensorData.longitude,SensorData.heading);
		dispatcher.dispatch(new TrackingEvent(type, capture,detection.getRegion()));
	} 

}
