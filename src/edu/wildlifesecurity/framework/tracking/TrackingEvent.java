package edu.wildlifesecurity.framework.tracking;

import org.opencv.core.Rect;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class TrackingEvent extends Event {
	
	public static EventType NEW_CAPTURE = new EventType("TrackingEvent.NewCapture");
	public static EventType NEW_TRACK = new EventType("TrackingEvent.NewTrack");
	
	private Capture capture;
	private Rect region;

	public TrackingEvent(EventType type, Capture capture, Rect region) {
		super(type);
		this.capture = capture;
		this.region = region;
	}

	public Capture getCapture(){
		return capture;
	}
	public Rect getRegion()
	{
		return this.region;
	}
}
