package edu.wildlifesecurity.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.wildlifesecurity.framework.Message.Commands;
import edu.wildlifesecurity.framework.actuator.IActuator;
import edu.wildlifesecurity.framework.communicatorserver.ICommunicatorServer;
import edu.wildlifesecurity.framework.repository.IRepository;
import edu.wildlifesecurity.framework.tracking.ITracking;

public class SurveillanceServerManager extends SurveillanceManager {
	
	protected ITracking analytics;
	private IActuator actuator;
	private IRepository repository;
	private ICommunicatorServer communicator;

	public SurveillanceServerManager(ITracking analytics, IActuator actuator, IRepository repository, ICommunicatorServer communicator) {

		super();
		
		this.analytics = analytics;
		this.actuator = actuator;
		this.repository = repository;
		this.communicator = communicator;
	}

	@Override
	public void start() {
		
		// Load components' configuration
		loadComponentsConfiguration();
		
		//  Init repository (and logger)
		repository.init();
		
		// Set logger
		analytics.loadLogger(repository);
		actuator.loadLogger(repository);
		communicator.loadLogger(repository);
		
		// Init components
		analytics.init();
		actuator.init();
		communicator.init();
		
		// TODO: Connect components (start listen for various messages etc...)
		
		// Redirect log requests
		communicator.addEventHandler(MessageEvent.getEventType(Commands.LOG), new IEventHandler<MessageEvent>(){

			@Override
			public void handle(MessageEvent event) {
				String[] args = event.getMessage().getMessage().split(",");
				switch(args[1]){
				case "INFO":
					repository.info(args[2]);
					break;
				case "WARN":
					repository.warn(args[2]);
					break;
				case "ERROR":
					repository.error(args[2]);
					break;
				}
				
			}
			
		});
		
	}
	
	@Override
	public void stop(){
		
		repository.dispose();
		
	}

	/*
	 * Loads current component configuration 
	 */
	private void loadComponentsConfiguration() {
		// Fetches all configuration from the Repository component and loads each components' configuration
		
		Map<String, Object> allConfiguration = new HashMap<String,Object>();
		Map<String, Object> communicatorConfig = new HashMap<String,Object>();
		Map<String, Object> analyticsConfig = new HashMap<String,Object>();
		Map<String, Object> actuatorConfig = new HashMap<String,Object>();
		
		repository.loadConfiguration(allConfiguration);
		
		for(Entry<String, Object> e : allConfiguration.entrySet()){
			switch(e.getKey().split("_")[0]){
			case "Analytics":
				analyticsConfig.put(e.getKey(), e.getValue());
				break;
			case "Actuator":
				actuatorConfig.put(e.getKey(), e.getValue());
				break;
			default:
				communicatorConfig.put(e.getKey(), e.getValue());
				break;
			}
		}
		
		analytics.loadConfiguration(analyticsConfig);
		actuator.loadConfiguration(actuatorConfig);
		communicator.loadConfiguration(communicatorConfig);
	}


}
