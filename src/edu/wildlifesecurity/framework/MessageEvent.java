package edu.wildlifesecurity.framework;


public class MessageEvent extends Event {
	
	public static EventType ANY = new EventType("MessageEvent.ANY");
	
	public static EventType getEventType(Message.Commands command){
		return new EventType("MessageEvent."+command);
	}
	public static EventType getEventType(String command){
		return new EventType("MessageEvent."+command);
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
