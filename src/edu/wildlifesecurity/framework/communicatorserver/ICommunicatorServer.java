package edu.wildlifesecurity.framework.communicatorserver;

import java.util.List;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.Message;
import edu.wildlifesecurity.framework.MessageEvent;

public interface ICommunicatorServer extends IComponent {
	
	/**
	 * Enables consumers to listen for messages. For example, a Repository implementation should listen to log messages.
	 */
	ISubscription addMessageEventHandler(EventType type, IEventHandler<MessageEvent> handler);
	
	/**
	 * Sends a message to a recipient that is contained in the Message instance.
	 */
	void sendMessage(Message message);
	
	/**
	 * Gets a list of currently connected trap devices
	 */
	List<TrapDevice> getConnectedTrapDevices();
	
	/**
	 * Enables consumers to get notified when a new TrapDevice has connected is disconnected
	 */
	ISubscription addConnectEventHandler(EventType type, IEventHandler<ConnectEvent> handler);

}
