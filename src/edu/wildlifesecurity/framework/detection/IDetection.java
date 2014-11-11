package edu.wildlifesecurity.framework.detection;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;

// input: raw image 
//output: image of animal 
public interface IDetection extends IComponent {
	
	ISubscription addEventHandler(EventType type, IEventHandler<DetectionEvent> handler);
	 
	Detections getObjInImage(Mat image);

}
