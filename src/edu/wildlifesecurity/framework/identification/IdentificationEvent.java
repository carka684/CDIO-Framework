package edu.wildlifesecurity.framework.identification;

import edu.wildlifesecurity.framework.Event;
import edu.wildlifesecurity.framework.EventType;

public class IdentificationEvent extends Event {
	
	public static final EventType NEW_IDENTIFICATION = new EventType("IdentificationEvent.NewIdentification");

	private IClassificationResult result;
	
	public IdentificationEvent(EventType type, IClassificationResult result) {
		super(type);
		this.result = result;
	}
	
	public IClassificationResult getResult(){
		return this.result;
	}

}
