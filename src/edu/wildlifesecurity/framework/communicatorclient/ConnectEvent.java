package edu.wildlifesecurity.framework.communicatorclient;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class ConnectEvent extends Event {

	public static EventType CONNECTED = new EventType("ConnectEvent.Connected");
	public static EventType DISCONNECTED = new EventType("ConnectEvent.Disconnected");

	private boolean isConnected;
	public ConnectEvent(EventType type, boolean isConnected) {
		super(type);
		this.isConnected=isConnected;
	}

	public boolean isConnected()
	{
		return isConnected;
	}
}
