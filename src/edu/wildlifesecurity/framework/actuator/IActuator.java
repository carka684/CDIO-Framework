package edu.wildlifesecurity.framework.actuator;

import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.tracking.Capture;

public interface IActuator extends IComponent {
	
	void actOnCapture(Capture capture);
	
}
