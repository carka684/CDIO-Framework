package edu.wildlifesecurity.framework.actuator.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.actuator.IActuator;
import edu.wildlifesecurity.framework.identification.Classes;
import edu.wildlifesecurity.framework.tracking.Capture;

/**
 * Sends alarm sms to configurable phone numbers when a rhino is detected. The sms is sent using a phone gateway, 
 * which has to have a gateway app (SMS Gateway) installed, and needs to be reachable in the network.
 * 
 * @author Tobias
 *
 */
public class DefaultActuator extends AbstractComponent implements IActuator {
	/*
	 * When using this class the following requirements needs to be fullfilled:
	 * A android phone running the app "SMS Gateway" which can be conntected (pinged) from the server.
	 * The setting in the app is matching the password and port arguments to sendMessage-funcation.
	 * The IP should not contain any http://, just XXX.XXX.XXX.XXX
	 * The message will be encoded to UTF-8 to handle spaces and other chars
	 * 
	 * Ex:
	 * SMSSender sms = new SMSSender();
	 * sms.sendMessage("130.236.227.213", "9090", "det här är ett sms med mellanslag", "0722312561", "123");
	 */
	
	private String IP;
	private int port;
	private String message;
	private String number;
	private String password;
	
	@Override
	public void init(){
		IP = configuration.get("Actuator_IP").toString();
		port = Integer.parseInt(configuration.get("Actuator_Port").toString());
		message = configuration.get("Actuator_Message").toString();
		number = configuration.get("Actuator_GuardNumber").toString();
		password = configuration.get("Actuator_Password").toString();
	}
	

	@Override
	public void actOnCapture(Capture capture) {
		if(capture.classification == Classes.RHINO){
			Vector<String> numVec  = new Vector<>();
			numVec.add("0722312561");
			numVec.add("0762662802");
			sendAlarmMessage_DEMO(numVec,capture);
			System.out.println("Actuator Alarm! Rhino was captured! " + capture.timeStamp);
		}else if(capture.classification == Classes.HUMAN){
			System.out.println("Actuator Alarm! Human was captured! " + capture.timeStamp);
		}
	}
	private void sendAlarmMessage_DEMO(Vector<String> numVec, Capture capture) {
		String messasge_demo = "Rhino detected in Filtret from TrapDevice: " + capture.trapDeviceId;
		for(String number : numVec)
		{
			try {
				String URLmessage = URLEncoder.encode(message, "UTF-8");
				String urlString = "http://" + IP + ":" + port + "/sendsms?phone=" + number + "&text=" +
						URLmessage + "&password=" + password;
				
				URL url;
				HttpURLConnection conn;
				BufferedReader rd;
				String line;
				String result = "";
				
				url = new URL(urlString);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while ((line = rd.readLine()) != null) {
					result += line;
				}
				rd.close();
	
			} catch (Exception e) {
				log.error("Error in DefaultActuator. Could not send alarm sms message: " + e.getMessage());
			}
		}

	}
	
	
	private void sendAlarmMessage() {
		try {
			String URLmessage = URLEncoder.encode(message, "UTF-8");
			String urlString = "http://" + IP + ":" + port + "/sendsms?phone=" + number + "&text=" +
					URLmessage + "&password=" + password;
			
			URL url;
			HttpURLConnection conn;
			BufferedReader rd;
			String line;
			String result = "";
			
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();

		} catch (Exception e) {
			log.error("Error in DefaultActuator. Could not send alarm sms message: " + e.getMessage());
		}

	}
}
