package edu.wildlifesecurity.framework.communicatorclient;

import java.util.Map;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ILogger;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.Message;
import edu.wildlifesecurity.framework.MessageEvent;

/**
 * The CommunicatorClient component sends and receives messages from the server. It also works as a Logger in a sense as it redirects log messages to the server for storing
 */
public interface ICommunicatorClient extends IComponent, ILogger {

	/**
	 * Enables consumers to listen for messages. For example, a Repository implementation should listen to log messages.
	 */
	ISubscription addEventHandler(EventType type, IEventHandler<MessageEvent> handler);
	
	/**
	 * Sends a message to the server.
	 */
	void sendMessage(Message message);
	
	/**
	 * Enables consumers to get notified when a server connection has been established or disconnected
	 */
	ISubscription addConnectionEventHandler(EventType type, IEventHandler<ConnectEvent> handler);

	/**
	 * Returns whether it is connected or not 
	 * @return
	 */
	boolean isConnected();
	
	/**
	 * Returns the configuration that has been loaded from the server
	 * @return
	 */
	Map<String,Object> getConfiguration();
	
	/**
	 * Disposes the communicator
	 */
	void dispose();
}
