package edu.wildlifesecurity.framework;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.Message.Commands;
import edu.wildlifesecurity.framework.communicatorclient.ICommunicatorClient;
import edu.wildlifesecurity.framework.detection.Detection;
import edu.wildlifesecurity.framework.detection.DetectionResult;
import edu.wildlifesecurity.framework.detection.IDetection;
import edu.wildlifesecurity.framework.identification.IIdentification;
import edu.wildlifesecurity.framework.mediasource.IMediaSource;
import edu.wildlifesecurity.framework.mediasource.MediaEvent;
import edu.wildlifesecurity.framework.tracking.Capture;
import edu.wildlifesecurity.framework.tracking.TrackingEvent;
import edu.wildlifesecurity.framework.tracking.impl.KalmanTracking;
import edu.wildlifesecurity.framework.tracking.impl.SerializableCapture;

public class SurveillanceClientManager extends SurveillanceManager {
	
	private IMediaSource mediaSource;
	private IDetection detection;
	private IIdentification identification;
	private ICommunicatorClient communicator;
	private KalmanTracking tracker;
	
	private List<ISubscription> subscriptions;
	private ILogger logger;
	
	public SurveillanceClientManager(IMediaSource mediaSource, IDetection detection, IIdentification identification,
									 ICommunicatorClient communicator, KalmanTracking tracker, ILogger logger){
		super();
		
		this.mediaSource = mediaSource;
		this.detection = detection;
		this.identification = identification;
		this.communicator = communicator;
		this.tracker = tracker;
		
		subscriptions = new LinkedList<ISubscription>();
		this.logger = logger;
	}
	
	/*
	 * Starts the client manager
	 */
	@Override
	public void start(){
		
		// First, connect to backend server to fetch components' configuration
		communicator.loadLogger(logger);
		communicator.init();
		subscriptions.add(communicator.addEventHandler(MessageEvent.getEventType(Commands.HANDSHAKE_ACK), new IEventHandler<MessageEvent>(){

			@Override
			public void handle(MessageEvent event) {
				
				// Set logger (CommunicatorClient instance)
				mediaSource.loadLogger(logger);
				detection.loadLogger(logger);
				identification.loadLogger(logger);
					
				// Load all components' configuration
				loadComponentsConfigutation();
				
				// Init all other components
				detection.init();
				identification.init();
				tracker.init();
				mediaSource.init();

				// Start listening for images from the MediaSource component
				subscriptions.add(mediaSource.addEventHandler(MediaEvent.NEW_SNAPSHOT, new IEventHandler<MediaEvent>(){
		
					@Override
					public void handle(MediaEvent event) {
						// We have got a new image from MediaSource, start process through components...
						processImage(event.getImage());				
					}
					
				}));
				
				// Start listening for new captures from tracking component
				subscriptions.add(tracker.addEventHandler(TrackingEvent.NEW_CAPTURE, new IEventHandler<TrackingEvent>(){

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
					
				}));
				
				// Start listen for configuration updates
				subscriptions.add(communicator.addEventHandler(MessageEvent.getEventType(Commands.SET_CONFIG), new IEventHandler<MessageEvent>(){

					@Override
					public void handle(MessageEvent event) {
						
						String[] messageParts = event.getMessage().getMessage().split(",");
						
						switch(messageParts[1].split("_")[0]){
						case "MediaSource":
							mediaSource.setConfigOption(messageParts[1], messageParts[2]);
							break;
						case "Detection":
							detection.setConfigOption(messageParts[1], messageParts[2]);
							break;
						case "Identification":
							identification.setConfigOption(messageParts[1], messageParts[2]);
							break;
						}
						
					}
					
				}));
				
			}
			
		}));
	}
	
	@Override
	public void stop(){
		// Unsubscribe all subscriptions
		for(ISubscription sub : subscriptions)
			sub.removeHandler();
		
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
		HashMap<String,Object> trackingConfig = new HashMap<String,Object>();
		
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
			case "Tracking":
				trackingConfig.put(entry.getKey(), entry.getValue());
				break;
			}
		}
		
		mediaSource.loadConfiguration(mediasourceConfig);
		detection.loadConfiguration(detectionConfig);
		identification.loadConfiguration(identificationConfig);
		tracker.loadConfiguration(trackingConfig);
		
		
		/// TEMPORARY! Hardcoded configuration
		/*HashMap<String, Object> mediaSourceConfig = new HashMap<String, Object>();
		mediaSourceConfig.put("MediaSource_FrameRate", 1000); // Sets the frame rate when the component should take pictures
		mediaSource.loadConfiguration(mediaSourceConfig);
		
		HashMap<String, Object> detectionConfig = new HashMap<String, Object>();
		detectionConfig.put("Detection_varThreshold", 20);
		detectionConfig.put("Detection_bShadowDetection", true);
		detectionConfig.put("Detection_InitTime", 10);
		detectionConfig.put("Detection_highLearningRate", 0.01);
		detectionConfig.put("Detection_lowLearningRate", 0.004);
		detectionConfig.put("Detection_numOperationsInOpening", 1);
		detectionConfig.put("Detection_numOperationsInClosing", 2);
		detectionConfig.put("Detection_MinSizeOfDetectedObjects", 500);
		detection.loadConfiguration(detectionConfig);
		
		HashMap<String, Object> identificationConfig = new HashMap<String, Object>();
		identificationConfig.put("Identification_imageSide", 240); // Height and width of the resized image
		identificationConfig.put("Identification_hog_blockSide", 16); // Side length of a block
		identificationConfig.put("Identification_hog_blockStrideSide", 8); // Side length of block stride. Must be a multiple of cellSide
		identificationConfig.put("Identification_hog_cellSide", 8); // Side length of a cell
		identificationConfig.put("Identification_hog_numberOfBins", 9); // Number of bins
		identificationConfig.put("Identification_libsvm_kernelType", 0); // Kernel type of libsvm, 0 = linear
		identificationConfig.put("Identification_libsvm_C", 16); // Cost parameter C of libsvm
		identificationConfig.put("Identification_libsvm_eps", 0.01); // Tolerance of termination criterion for libsvm
		identificationConfig.put("Identification_numberOfClasses", 3); // Number of classes that can be identified
		identificationConfig.put("Identification_Classifier0", "/storage/sdcard0/wRhinoOther.txt"); // Path to classifier plane for rhino vs other
		identificationConfig.put("Identification_Classifier1", "/storage/sdcard0/wHumanOther.txt"); // Path to classifier plane for human vs other
		identificationConfig.put("Identification_Classifier2", "/storage/sdcard0/wRhinoHuman.txt"); // Path to classifier plane for rhino vs human
		identification.loadConfiguration(identificationConfig);
		
		HashMap<String, Object> trackingConfig = new HashMap<String, Object>();
		trackingConfig.put("Tracking_max_predict_pos_error", 80); //Maximum distance between prediction and true center
		trackingConfig.put("Tracking_max_predict_height_error", 0.5); //Minimum ratio between predicted height and true height allowed
		trackingConfig.put("Tracking_max_predict_width_error", 0.5); //Minimum ratio between predicted width and true width allowed
		trackingConfig.put("Tracking_num_of_missing_frames", 7);//Number of frames a kalmanfilter can be unmatched with a detection before removal
		trackingConfig.put("Tracking_ratio_of_same_classification", 0.7);//Minimum ratio of the most common class for each kalman filter for a capture to be sent
		trackingConfig.put("Tracking_num_of_seen_frames", 10);//Minimum frames the same detection has been seen for a capture to be sent.
		tracker.loadConfiguration(trackingConfig);*/
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
