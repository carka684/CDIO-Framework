package edu.wildlifesecurity.framework.repository;

import java.util.Date;
import java.util.List;

import org.opencv.core.Mat;

import edu.wildlifesecurity.framework.EventType;
import edu.wildlifesecurity.framework.IComponent;
import edu.wildlifesecurity.framework.IEventHandler;
import edu.wildlifesecurity.framework.ILogger;
import edu.wildlifesecurity.framework.ISubscription;
import edu.wildlifesecurity.framework.LogEvent;
import edu.wildlifesecurity.framework.tracking.Capture;

/**
 * A repository component handles storing of data. Data such at logging and configuration.
 * 
 */
public interface IRepository extends IComponent, ILogger {
	
	/**
	 * Enables listeners to receive events when log entries are made
	 * @param type
	 * @param handler
	 * @return
	 */
	ISubscription addEventHandler(EventType type, IEventHandler<LogEvent> handler);

	/**
	 * Stores the given capture in the repository
	 * 
	 * @param capture
	 */
	void storeCapture(Capture capture);
	
	/**
	 * Loads all captures from all trap devices from repository, excluding their images
	 * 
	 * @return A list of captures
	 */
	List<Capture> getCaptureDefinitions();
	
	/**
	 * Fetches the detection image for the given captureId from the repository
	 * 
	 * @param captureId
	 * @return
	 */
	Mat getCaptureImage(Capture captureId);
	
	/**
	 * Gets a config entry
	 * 
	 * @param option
	 * @author lukas
	 * @return Object
	 */
	Object getConfigOption(String option);

	/**
	 * Sets a config entry
	 * 
	 * @param option
	 * @param value
	 * @author lukas
	 */
	void setConfigOption(String option, Object value);
	
	
	/**
	 * Disposes the repository
	 */
	void dispose();
	
	/**
	 * Writes entry to log with priority prio and message msg
	 * @param prio
	 * @param msg
	 * 
	 */
	void log(String prio, String msg);
	
	/**
	 * Get log entries from log that is between start time and end time
	 * @param startTime
	 * @param endTime
	 * @author lukas
	 */
	public String getLog(Date startTime, Date endTime);
	
	/**
	 * Get log entries from log that is after start time.
	 * @param startTime
	 * @author lukas
	 */
	public String getLog(Date startTime);
	
}
