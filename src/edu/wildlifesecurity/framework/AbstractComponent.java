package edu.wildlifesecurity.framework;

import java.util.Map;

public abstract class AbstractComponent implements IComponent {
	
	protected Map<String, Object> configuration;
	protected ILogger log;
	
	public void init(){
		
	}
	
	@Override
	public void loadConfiguration(Map<String, Object> config) {
		configuration = config;
	}
	
	@Override
	public void setConfigOption(String key, String value){
		configuration.put(key, value);
	}
	
	@Override
	public void loadLogger(ILogger logger){
		log = logger;
	}

}
