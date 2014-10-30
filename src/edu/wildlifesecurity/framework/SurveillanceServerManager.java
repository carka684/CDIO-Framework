package edu.wildlifesecurity.framework;

import java.util.List;

import edu.wildlifesecurity.framework.actuator.IActuator;
import edu.wildlifesecurity.framework.analytics.IAnalytics;
import edu.wildlifesecurity.framework.communicatorserver.ICommunicatorServer;
import edu.wildlifesecurity.framework.repository.IRepository;

public class SurveillanceServerManager extends SurveillanceManager {
	
	private IAnalytics analytics;
	private IActuator actuator;
	private IRepository repository;
	private ICommunicatorServer communicator;
	
	private List<ISystemInterface> systemInterfaces;

	public SurveillanceServerManager(IAnalytics analytics, IActuator actuator, IRepository repository, ICommunicatorServer communicator, List<ISystemInterface> systemInterfaces) {

		super();
		
		this.analytics = analytics;
		this.actuator = actuator;
		this.repository = repository;
		this.communicator = communicator;	
		this.systemInterfaces = systemInterfaces;
	}

	@Override
	void start() {
		
		// Load components' configuration
		loadComponentsConfiguration();

		// Link System Interfaces
		for(ISystemInterface sysInt : systemInterfaces)
			sysInt.link(repository);
		
		// Init components
		
	}

	/*
	 * Loads current component configuration 
	 */
	private void loadComponentsConfiguration() {
		// Fetches all configuration from the Repository component and loads each components' configuration
		
	}


}
