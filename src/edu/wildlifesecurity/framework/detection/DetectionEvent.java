package edu.wildlifesecurity.framework.detection;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class DetectionEvent extends Event {
	
	public static final EventType NEW_DETECTION = new EventType("Detection.NewDetection");
	
	private DetectionResult detection;

	public DetectionEvent(EventType type, DetectionResult detection) {
		super(type);
		this.detection = detection;
	}

	public DetectionResult getDetectionResult(){
		return this.detection;
	}
	
}
