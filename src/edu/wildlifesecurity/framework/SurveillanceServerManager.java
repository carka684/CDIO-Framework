package edu.wildlifesecurity.framework;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Base64;

import edu.wildlifesecurity.framework.Message.Commands;
import edu.wildlifesecurity.framework.actuator.IActuator;
import edu.wildlifesecurity.framework.communicatorserver.ICommunicatorServer;
import edu.wildlifesecurity.framework.repository.IRepository;
import edu.wildlifesecurity.framework.tracking.Capture;
import edu.wildlifesecurity.framework.tracking.ITracking;

public class SurveillanceServerManager extends SurveillanceManager {
	
	private IActuator actuator;
	private IRepository repository;
	private ICommunicatorServer communicator;

	public SurveillanceServerManager(IActuator actuator, IRepository repository, ICommunicatorServer communicator) {

		super();
		
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
		actuator.loadLogger(repository);
		communicator.loadLogger(repository);
		
		// Init components
		actuator.init();
		communicator.init();
		
		/// Adds listeners for various message types
		
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
		
		// Handle NEW_CAPTURE messages
		communicator.addEventHandler(MessageEvent.getEventType(Commands.NEW_CAPTURE), new IEventHandler<MessageEvent>(){

			@Override
			public void handle(MessageEvent event) {

				// When a new capture is received, save it to repository and send it to actuator that decides how to act on it

				Capture capture = null;
				
				try{
					// Parse capture
					String captureEncoded = event.getMessage().getMessage().split(",")[1];
					byte[] bytes = Base64.getDecoder().decode(captureEncoded);
					ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
					ObjectInput ois = new ObjectInputStream(bis);
					capture = (Capture) ois.readObject();
					
				}catch(Exception e){
					repository.error("Error in SurveillanceServerManager. Cannot deserialize capture: " + e.getMessage());
					return;
				}
				
				// Send to actuator
				actuator.actOnCapture(capture);
				
				// Store in repository
				repository.storeCapture(capture);
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
		Map<String, Object> actuatorConfig = new HashMap<String,Object>();
		
		repository.loadConfiguration(allConfiguration);
		
		for(Entry<String, Object> e : allConfiguration.entrySet()){
			switch(e.getKey().split("_")[0]){
			case "Actuator":
				actuatorConfig.put(e.getKey(), e.getValue());
				break;
			default:
				communicatorConfig.put(e.getKey(), e.getValue());
				break;
			}
		}
		
		actuator.loadConfiguration(actuatorConfig);
		communicator.loadConfiguration(communicatorConfig);
	}


}
