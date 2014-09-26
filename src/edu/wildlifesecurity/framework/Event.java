package edu.wildlifesecurity.framework;


/**
 * Basic implementation of the IEvent interface
 * 
 * @author Tobias
 *
 */
public abstract class Event implements IEvent {

	private EventType type;
	
	protected Event(EventType type){
		this.type = type;
	}
	
	@Override
	public EventType getType() {
		return type;
	}

}
