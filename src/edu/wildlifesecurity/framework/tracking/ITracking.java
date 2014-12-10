package edu.wildlifesecurity.framework.tracking;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.detection.DetectionResult;

public interface ITracking extends IComponent {
	/**
	 * Enables subscription of TrackingEvent
	 */
	ISubscription addEventHandler(EventType type, IEventHandler<TrackingEvent> handler);
	/**
	 * Tracks all the regions in a DetectionResult 
	 * and sends a TrackingEvent when a object has been
	 * seen enough times and been the same class most 
	 * of the time
	 */
	void trackRegions(DetectionResult detections);
}
