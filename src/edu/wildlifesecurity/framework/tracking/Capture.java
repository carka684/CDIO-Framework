package edu.wildlifesecurity.framework.tracking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Mat;

public class Capture {
	
	private String pathFormat= "Captures/%s/%d.png";
	
	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public int captureId;

	public Date timeStamp;
	
	public int trapDeviceId;
	
	public String position;
	
	public Mat image;
	
	public String imagePath;

	public Capture() {
		super();
	}
	
	public Capture(int captureId, Date timeStamp, int trapDeviceId,
			String position, Mat image) {
		super();
		this.captureId = captureId;
		this.timeStamp = timeStamp;
		this.trapDeviceId = trapDeviceId;
		this.position = position;
		this.image = image;
		this.imagePath = String.format(pathFormat,df.format(this.timeStamp),this.captureId);
	}
	
	public Capture(int captureId, Date timeStamp, int trapDeviceId,
			String position) {
		super();
		this.captureId = captureId;
		this.timeStamp = timeStamp;
		this.trapDeviceId = trapDeviceId;
		this.position = position;
		this.imagePath = String.format(pathFormat,df.format(this.timeStamp),this.captureId);
	}
	
	public Capture(int captureId, Date timeStamp, int trapDeviceId,
			String position, Mat image, String path) {
		super();
		this.captureId = captureId;
		this.timeStamp = timeStamp;
		this.trapDeviceId = trapDeviceId;
		this.position = position;
		this.image = image;
		this.imagePath = path;
	}


	/**
	 * @return the captureId
	 */
	public int getCaptureId() {
		return captureId;
	}

	/**
	 * @param captureId the captureId to set
	 */
	public void setCaptureId(int captureId) {
		this.captureId = captureId;
	}

	/**
	 * @return the timeStamp
	 */
	public Date getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the trapDeviceId
	 */
	public int getTrapDeviceId() {
		return trapDeviceId;
	}

	/**
	 * @param trapDeviceId the trapDeviceId to set
	 */
	public void setTrapDeviceId(int trapDeviceId) {
		this.trapDeviceId = trapDeviceId;
	}

	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the image
	 */
	public Mat getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Mat image) {
		this.image = image;
	}
	
	/**
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	
}
