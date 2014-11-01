package edu.wildlifesecurity.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventDispatcher<T extends IEvent> {
	
	private HashMap<EventType, List<IEventHandler<T>>> handlers = new HashMap<EventType, List<IEventHandler<T>>>();

	public ISubscription addEventHandler(final EventType type, final IEventHandler<T> handler){

		if(!handlers.containsKey(type)){
			handlers.put(type, new ArrayList<IEventHandler<T>>());
		}
		handlers.get(type).add(handler);
		
		return new ISubscription() {

			@Override
			public void removeHandler() {
				handlers.get(type).remove(handler);
			}

		};
		
	}
	
	public void dispatch(T event){
		
		List<IEventHandler<T>> handl = handlers.get(event.getType());
		
		if(handl != null){
			for(IEventHandler<T> handler : handl){
				handler.handle(event);
			}
		}
	}
	
}
