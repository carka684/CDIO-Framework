package edu.wildlifesecurity.framework.mediasource;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;

public interface IMediaSource extends IComponent {

	ISubscription addEventHandler(EventType type, IEventHandler<MediaEvent> handler);
	
	Mat takeSnapshot();
	
	void destroy();

}
