package edu.wildlifesecurity.framework;

import edu.wildlifesecurity.framework.actuator.IActuator;
import edu.wildlifesecurity.framework.analytics.IAnalytics;
import edu.wildlifesecurity.framework.communicatorclient.ICommunicatorClient;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.mediasource.IMediaSource;
import edu.wildlifesecurity.framework.repository.IRepository;

public class SurveillanceClientManager extends SurveillanceManager {
	
	public SurveillanceClientManager(IMediaSource mediaSource, IDetection detection, IIdentification identification,
									 IAnalytics analytics, IActuator actuator, IRepository repository, ICommunicatorClient communicator){
		

	}
	
	/*
	 * Implements the 
	 */
	@Override
	public void process(){
		
	}
	
}
