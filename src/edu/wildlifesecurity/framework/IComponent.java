package edu.wildlifesecurity.framework;

import java.util.List;
import java.util.Map;

/*
 * General interface for all software components. Interfaces for functionality like logging and configuration
 * 
 */
public interface IComponent {

	void loadConfiguration(Map<String, Object> config);
	
}
