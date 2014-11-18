package edu.wildlifesecurity.framework;

public class Message {
	
	public enum Commands {
		HANDSHAKE_REQ("HANDSHAKE_REQ"),
		HANDSHAKE_ACK("HANDSHAKE_ACK"),
		LOG("LOG"),
		NEW_CAPTURE("NEW_CAPTURE");
		
		private final String name;       
	    private Commands(String s) {
	        name = s;
	    }
	    public String toString(){
	       return name;
	    }
	}
	
	private int receiver;
	private int sender;
	private String message;
	
	public Message(int receiver, String message){
		this.receiver = receiver;
		this.message = message;
	}
	
	public Message(String message, int sender){
		this.sender = sender;
		this.message = message;
	}
	
	public int getReceiver(){
		return this.receiver;
	}
	
	public int getSender(){
		return this.sender;
	}
	
	public String getMessage(){
		return this.message;
	}

}
