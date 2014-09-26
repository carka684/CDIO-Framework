package edu.wildlifesecurity.framework.mediasource;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;

public interface IMediaSource extends IComponent {

	void addEventHandler(EventType type, IEventHandler<MediaEvent> handler);
	
	Mat takeSnapshot();

}
