package edu.wildlifesecurity.framework;


public class MessageEvent extends Event {
	
	private Message message;

	protected MessageEvent(EventType type, Message message) {
		super(type);
		this.message = message;
	}
	
	public Message getMessage(){
		return message;
	}

}
