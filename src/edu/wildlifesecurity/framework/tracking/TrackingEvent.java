package edu.wildlifesecurity.framework.tracking;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class TrackingEvent extends Event {
	
	public static EventType NEW_CAPTURE = new EventType("TrackingEvent.NewCapture");
	public static EventType NEW_TRACK = new EventType("TrackingEvent.NewTrack");
	
	private Capture capture;

	public TrackingEvent(EventType type, Capture capture) {
		super(type);
		this.capture = capture;
	}

	public Capture getCapture(){
		return capture;
	}
}
