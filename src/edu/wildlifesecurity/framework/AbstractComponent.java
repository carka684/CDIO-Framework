package edu.wildlifesecurity.framework;

import java.util.Map;

public abstract class AbstractComponent implements IComponent {
	
	protected Map<String, Object> configuration;
	
	public void init(){
		
	}
	
	@Override
	public void loadConfiguration(Map<String, Object> config) {
		configuration = config;
	}

}
