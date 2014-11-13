package edu.wildlifesecurity.framework.tracking;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;

public interface ITracking extends IComponent {

	ISubscription addEventHandler(EventType type, IEventHandler<TrackingEvent> handler);
	
}
