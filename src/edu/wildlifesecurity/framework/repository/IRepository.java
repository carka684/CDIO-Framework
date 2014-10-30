package edu.wildlifesecurity.framework.repository;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ILogger;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.LogEvent;

/**
 * A repository component handles storing of data. Data such at logging and configuration.
 * 
 */
public interface IRepository extends IComponent, ILogger {
	
	/**
	 * Enables listeners to receive events when log entries are made
	 * @param type
	 * @param handler
	 * @return
	 */
	ISubscription addEventHandler(EventType type, IEventHandler<LogEvent> handler);

	/**
	 * Disposes the repository
	 */
	void dispose();
	
}
