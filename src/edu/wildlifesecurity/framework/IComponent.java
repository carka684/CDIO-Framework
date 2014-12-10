package edu.wildlifesecurity.framework;

import java.util.List;
import java.util.Map;

/**
 * General interface for all software components. Interfaces for functionality like logging and configuration
 * 
 */
public interface IComponent {
	
	/**
	 * Initializes the component
	 */
	void init();

	/**
	 * Loads a map of configuration options into the component
	 * @param config The map of configuration options
	 */
	void loadConfiguration(Map<String, Object> config);
	
	/**
	 * Updates the value of a configuration option
	 */
	void setConfigOption(String key, String value);
	
	/**
	 * Sets a ILogger instance that this component should use to log messages
	 */
	void loadLogger(ILogger logger);
	
}
