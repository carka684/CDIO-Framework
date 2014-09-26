package edu.wildlifesecurity.framework.mediasource;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IEvent;

/**
 * An media event originating from the MediaSource component, for example a new image 
 * has been taken
 * 
 * @author Tobias
 *
 */
public class MediaEvent extends Event {

	public static EventType NEW_SNAPSHOT = new EventType("MediaEvent.NewSnapshot");
	
	public MediaEvent(EventType type, Mat image){
		super(type);
		this.image = image;
	}
	
	private Mat image;
	
	/**
	 * Gets the image associated with this MediaEvent
	 * 
	 * @return
	 */
	public Mat getImage(){
		return image;
	}

}
