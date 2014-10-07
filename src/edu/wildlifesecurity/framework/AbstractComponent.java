package edu.wildlifesecurity.framework;

import java.util.Map;

public abstract class AbstractComponent implements IComponent {
	
	protected Map<String, Object> configuration;
	
	@Override
	public void loadConfiguration(Map<String, Object> config) {
		configuration = config;
	}

}
