package edu.wildlifesecurity.framework.communicatorserver;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class ConnectEvent extends Event {

	public TrapDevice trapDevice;
	public ConnectEvent(EventType type, TrapDevice trapDevice) {
		super(type);
		this.trapDevice=trapDevice;
	}

}
