package edu.wildlifesecurity.framework.actuator;

import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.tracking.Capture;

public interface IActuator extends IComponent {
	/**
	 * Act according to the content of the capture
	 */
	void actOnCapture(Capture capture);
	
}
