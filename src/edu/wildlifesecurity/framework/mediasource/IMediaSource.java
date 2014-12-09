package edu.wildlifesecurity.framework.mediasource;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;

public interface IMediaSource extends IComponent {

	/**
	 * Enables subscription of MediaEvents
	 */
	ISubscription addEventHandler(EventType type, IEventHandler<MediaEvent> handler);
	
	/**
	 * Requests the MediaSource to take a snapshot and return it as an OpenCV Mat object
	 * @return The picture
	 */
	Mat takeSnapshot();
	
	/**
	 * Destroys the MediaSource and releases held resources
	 */
	void destroy();

}
