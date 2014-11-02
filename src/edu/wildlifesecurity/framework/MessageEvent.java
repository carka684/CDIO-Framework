package edu.wildlifesecurity.framework;

import java.util.HashMap;


public class MessageEvent extends Event {
	
	public static EventType ANY = new EventType("MessageEvent.ANY");
	
	private static HashMap<String,EventType> eventTypes = new HashMap<String, EventType>();
	
	public static EventType getEventType(Message.Commands command){
		if(!eventTypes.containsKey(command.toString()))
			eventTypes.put(command.toString(), new EventType("MessageEvent."+command));
		return eventTypes.get(command.toString());
	}
	public static EventType getEventType(String command){
		if(!eventTypes.containsKey(command))
			eventTypes.put(command, new EventType("MessageEvent."+command));
		return eventTypes.get(command);
	}
	
	private Message message;

	public MessageEvent(EventType type, Message message) {
		super(type);
		this.message = message;
	}
	
	public Message getMessage(){
		return message;
	}

}
