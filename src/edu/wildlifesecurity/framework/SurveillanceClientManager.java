package edu.wildlifesecurity.framework;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.xml.bind.DatatypeConverter;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.Message.Commands;
import edu.wildlifesecurity.framework.communicatorclient.ICommunicatorClient;
import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.identification.IClassificationResult;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.mediasource.IMediaSource;
import edu.wildlifesecurity.framework.mediasource.MediaEvent;
import edu.wildlifesecurity.framework.tracking.Capture;
import edu.wildlifesecurity.framework.tracking.ITracking;
import edu.wildlifesecurity.framework.tracking.TrackingEvent;
import edu.wildlifesecurity.framework.tracking.impl.KalmanTracking;
import edu.wildlifesecurity.framework.tracking.impl.SerializableCapture;

public class SurveillanceClientManager extends SurveillanceManager {
	
	private IMediaSource mediaSource;
	private IDetection detection;
	private IIdentification identification;
	private ICommunicatorClient communicator;
	private KalmanTracking tracker;
	
	public SurveillanceClientManager(IMediaSource mediaSource, IDetection detection, IIdentification identification,
									 ICommunicatorClient communicator, KalmanTracking tracker){
		super();
		
		this.mediaSource = mediaSource;
		this.detection = detection;
		this.identification = identification;
		this.communicator = communicator;
		this.tracker = tracker;
	}
	
	/*
	 * Starts the client manager
	 */
	@Override
	public void start(){
		
		// First, connect to backend server to fetch components' configuration
		communicator.init();
		communicator.addEventHandler(MessageEvent.getEventType(Commands.HANDSHAKE_ACK), new IEventHandler<MessageEvent>(){

			@Override
			public void handle(MessageEvent event) {
				
				// Set logger (CommunicatorClient instance)
				mediaSource.loadLogger(communicator);
				detection.loadLogger(communicator);
				identification.loadLogger(communicator);
					
				// TODO: Load all components' configuration
				loadComponentsConfigutation();
				
				// Init all other components
				detection.init();
				identification.init();
				tracker.init();
				mediaSource.init();

				// Start listening for images from the MediaSource component
				mediaSource.addEventHandler(MediaEvent.NEW_SNAPSHOT, new IEventHandler<MediaEvent>(){
		
					@Override
					public void handle(MediaEvent event) {
						// We have got a new image from MediaSource, start process through components...
						processImage(event.getImage());				
					}
					
				});
				
				// Start listening for new captures from tracking component
				tracker.addEventHandler(TrackingEvent.NEW_CAPTURE, new IEventHandler<TrackingEvent>(){

					@Override
					public void handle(TrackingEvent event) {
						
						// 4. Send results of tracking to server using communicator component
												
						try {
							Message m = serializeCapture(event.getCapture());
							System.out.println(m.getMessage());
							communicator.sendMessage(m);
						} catch (Exception e) {
							// Write log message
							System.out.println("Error in SurveillanceClientManager. Could not serialize capture: " + e.getMessage());
							communicator.error("Error in SurveillanceClientManager. Could not serialize capture: " + e.getMessage());
						}
					}
					
				});
				
			}
			
		});
	}
	
	@Override
	public void stop(){
		// Closes the connection to server, if there is one, and terminates background connecting thread
		communicator.dispose();
		
		// Stops timer that takes pictures
		mediaSource.destroy();
		
	}
	
	/**
	 * Processes as image
	 */
	private void processImage(Mat image){
		
		// 1. Use detection component to detect stuff in the image
		DetectionResult objects = detection.getObjInImage(image);
		
		// 2. Use identification component to identify things in the image
		for(Detection obj : objects.getVector()){
			obj.setClassification(identification.classify(obj.getRegionImage()));
			System.out.println("Identified: " + obj.getClassification());
		}
		
		// 3. Use tracking component to track identified objects
		try {
			tracker.trackRegions(objects); // Dispatches NEW_CAPTURE events when it detects new captures
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	/**
	 *  Fetches components' configuration entries from server using CommunicatorClient component
	 */
	private void loadComponentsConfigutation(){
		
		HashMap<String,Object> mediasourceConfig = new HashMap<String,Object>();
		HashMap<String,Object> detectionConfig = new HashMap<String,Object>();
		HashMap<String,Object> identificationConfig = new HashMap<String,Object>();
		
		for(Entry<String,Object> entry : communicator.getConfiguration().entrySet()){
			switch(entry.getKey().split("_")[0]){
			case "MediaSource":
				mediasourceConfig.put(entry.getKey(), entry.getValue());
				break;
			case "Detection":
				detectionConfig.put(entry.getKey(), entry.getValue());
				break;
			case "Identification":
				identificationConfig.put(entry.getKey(), entry.getValue());
				break;
			}
		}
		
		mediaSource.loadConfiguration(mediasourceConfig);
		detection.loadConfiguration(detectionConfig);
		identification.loadConfiguration(identificationConfig);
		
		
		/// TEMPORARY! Hardcoded configuration
		/*Map<String, Object> mediaSourceConfig = new HashMap<String, Object>();
		mediaSourceConfig.put("MediaSource_FrameRate", 1000); // Sets the frame rate when the component should take pictures
		mediaSource.loadConfiguration(mediaSourceConfig);
		
		Map<String, Object> detectionConfig = new HashMap<String, Object>();
		detectionConfig.put("Detection_InitTime", 10);
		detection.loadConfiguration(detectionConfig);
		
		Map<String, Object> identificationConfig = new HashMap<String, Object>();

		identificationConfig.put("Identification_Classifier0", "/storage/sdcard0/primalVariableRhinoOther.txt");
		identificationConfig.put("Identification_Classifier1", "/storage/sdcard0/primalVariableHumanOther.txt");
		identificationConfig.put("Identification_Classifier2", "/storage/sdcard0/primalVariableRhinoHuman.txt");
		identification.loadConfiguration(identificationConfig);

		identificationConfig.put("Identification_Classifier", "/storage/sdcard0/primalVariable.txt");
		identification.loadConfiguration(identificationConfig);*/
	}
	
	/**
	 * Serializes a Capture object to a Message that can be sent to server using the CommunicatorClient
	 * 
	 * @param capture
	 * @return
	 * @throws IOException
	 */
	private Message serializeCapture(Capture capture) throws IOException {
		String message = "NEW_CAPTURE,";
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(new SerializableCapture(capture));
		
		message += Base64.encodeToString(os.toByteArray(), Base64.NO_WRAP);

		return new Message(0, message);
	}
	
}
