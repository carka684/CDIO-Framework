package edu.wildlifesecurity.framework;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Base64;

import edu.wildlifesecurity.framework.Message.Commands;
import edu.wildlifesecurity.framework.actuator.IActuator;
import edu.wildlifesecurity.framework.communicatorserver.ICommunicatorServer;
import edu.wildlifesecurity.framework.repository.IRepository;
import edu.wildlifesecurity.framework.tracking.Capture;
import edu.wildlifesecurity.framework.tracking.ITracking;
import edu.wildlifesecurity.framework.tracking.impl.SerializableCapture;

public class SurveillanceServerManager extends SurveillanceManager {
	
	private IActuator actuator;
	private IRepository repository;
	private ICommunicatorServer communicator;
	
	private Map<Integer, LinkedList<Entry<String, Object>>> configChanges = new HashMap<Integer,LinkedList<Entry<String,Object>>>();

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
		communicator.addMessageEventHandler(MessageEvent.getEventType(Commands.LOG), new IEventHandler<MessageEvent>(){

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
		communicator.addMessageEventHandler(MessageEvent.getEventType(Commands.NEW_CAPTURE), new IEventHandler<MessageEvent>(){

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
					capture = ((SerializableCapture) ois.readObject()).getCapture();
					Integer newCaptureNumber=repository.getCaptureDefinitions().size() + 1;
					capture.id=newCaptureNumber;
					capture.trapDeviceId=event.getMessage().getSender();
					
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

		// Store TrapDevice configuration changes to be able to know individual traps' config
		communicator.addMessageEventHandler(MessageEvent.getEventType(Commands.SET_CONFIG), new IEventHandler<MessageEvent>(){

			@Override
			public void handle(MessageEvent event) {
				// Store config change
				if(!configChanges.containsKey(event.getMessage().getReceiver()))
					configChanges.put(event.getMessage().getReceiver(), new LinkedList<Entry<String,Object>>());
				
				String[] messageParts = event.getMessage().getMessage().split(",");
				configChanges.get(event.getMessage().getReceiver()).add(new MapEntry<String, Object>(messageParts[1], messageParts[2]));
			}
			
		});
	}
	
	@Override
	public void stop(){
		
		repository.dispose();
		
	}
	
	/**
	 * Returns the configuration the given trap
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getTrapDeviceConfiguration(int id){
		// Load configuration from repository
		Map<String, Object> conf = new HashMap<String,Object>();
		repository.loadConfiguration(conf);
		
		// Remove non-trapdevice config options
		for(Entry<String, Object> e : conf.entrySet()){
			switch(e.getKey().split("_")[0]){
			case "MediaSource":
			case "Detection":
			case "Identification":
			case "Tracking":
				break;
			default:
				conf.remove(e.getKey());
				break;
			}
		}
		
		// Replay changes that has been made
		for(Entry<String, Object> e : configChanges.get(id)){
			conf.put(e.getKey(), e.getValue());
		}
		
		return conf;
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
