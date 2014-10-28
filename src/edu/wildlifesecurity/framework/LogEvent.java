package edu.wildlifesecurity.framework;

public class LogEvent extends Event {
	
	public static EventType INFO = new EventType("LogEvent.Info");
	public static EventType WARN = new EventType("LogEvent.Warn");
	public static EventType ERROR = new EventType("LogEvent.Error");

	protected LogEvent(EventType type, String message) {
		super(type);
		this.message = message;
	}

	private String message;
	
	public String getMessage(){
		return message;
	}
}
