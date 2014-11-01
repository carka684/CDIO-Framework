package edu.wildlifesecurity.framework;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.communicatorclient.ICommunicatorClient;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.mediasource.IMediaSource;
import edu.wildlifesecurity.framework.mediasource.MediaEvent;

public class SurveillanceClientManager extends SurveillanceManager {
	
	private IMediaSource mediaSource;
	private IDetection detection;
	private IIdentification identification;
	private ICommunicatorClient communicator;
	
	public SurveillanceClientManager(IMediaSource mediaSource, IDetection detection, IIdentification identification,
									 ICommunicatorClient communicator){
		super();
		
		this.mediaSource = mediaSource;
		this.detection = detection;
		this.identification = identification;
		this.communicator = communicator;
	}
	
	/*
	 * Starts the client manager
	 */
	@Override
	public void start(){
		
		// Set logger (CommunicatorClient instance)
		mediaSource.loadLogger(communicator);
		detection.loadLogger(communicator);
		identification.loadLogger(communicator);
			
		// TODO: Load all components' configuration
		loadComponentsConfigutation();
		
		// Init all components
		mediaSource.init();
		detection.init();
		identification.init();
		//communicator.init();
		
		// Start listening for images from the MediaSource component
		mediaSource.addEventHandler(MediaEvent.NEW_SNAPSHOT, new IEventHandler<MediaEvent>(){

			@Override
			public void handle(MediaEvent event) {
				//processImage(event.getImage());				
			}
			
		});
		
		// 
		mediaSource.takeSnapshot();
		
	}
	
	/**
	 * Processes as image
	 */
	private void processImage(Mat image){
		
		// Use detection component to detect stuff in the image
		Vector<Mat> objects = detection.getObjInImage(image);
		
		// Use identification component to identify things in the image
		List<IClassificationResult> results = new LinkedList<IClassificationResult>();
		for(Mat obj : objects){
			IClassificationResult result = identification.classify(obj);
			if(result.getResultingClass() != Classes.UNIDENTIFIED)
				results.add(result);
		}
		
		// TODO: Use communication component to send and proceed the processing on the server
		
	}
	
	/**
	 *  Fetches components' configuration entries from server using CommunicatorClient component
	 */
	private void loadComponentsConfigutation(){
		
		/// TEMPORARY! Hardcoded configuration
		Map<String, Object> mediaSourceConfig = new HashMap<String, Object>();
		mediaSourceConfig.put("MediaSource_FrameRate", 500); // Sets the frame rate when the component should take pictures
		mediaSource.loadConfiguration(mediaSourceConfig);
		
		Map<String, Object> detectionConfig = new HashMap<String, Object>();
		detectionConfig.put("Detection_InitTime", 500);
		detection.loadConfiguration(detectionConfig);
		
	}
	
}
