package edu.wildlifesecurity.framework.detection;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class DetectionEvent extends Event {
	
	public static final EventType NEW_DETECTION = new EventType("Detection.NewDetection");
	
	private Detections detection;

	public DetectionEvent(EventType type, Detections detection) {
		super(type);
		this.detection = detection;
	}

	public Detections getDetectionResult(){
		return this.detection;
	}
	
}
