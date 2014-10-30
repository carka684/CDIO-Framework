package edu.wildlifesecurity.framework;

public class Message {
	
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
