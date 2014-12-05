package edu.wildlifesecurity.framework.communicatorserver;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class ConnectEvent extends Event {

	public static EventType NEW_TRAPDEVICE = new EventType("ConnectEvent.NewTrapDevice");
	
	public static EventType DISCONNECT_TRAPDEVICE = new EventType("ConnectEvent.DisconnectTrapDevice");

	private TrapDevice trapDevice;
	public ConnectEvent(EventType type, TrapDevice trapDevice) {
		super(type);
		this.trapDevice=trapDevice;
	}

	public TrapDevice getTrapDevice()
	{
		return trapDevice;
	}
}
