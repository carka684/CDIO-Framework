package edu.wildlifesecurity.framework;

public interface ILogger {
	
	ISubscription addEventHandler(EventType type, IEventHandler<LogEvent> handler);
	
	void info(String message);
	void warn(String message);
	void error(String message);
	
}
