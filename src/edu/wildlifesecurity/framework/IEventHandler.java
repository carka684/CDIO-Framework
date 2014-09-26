package edu.wildlifesecurity.framework;

public interface IEventHandler<T extends IEvent> {
	public void handle(T event);
}
