package edu.wildlifesecurity.framework.actuator.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import edu.wildlifesecurity.framework.AbstractComponent;
import edu.wildlifesecurity.framework.actuator.IActuator;

public class SMSSender extends AbstractComponent implements IActuator {
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
	
	@Override
	public void sendMessage(String IP,String port, String message, String number, String password) {
		String URLmessage = "no message";
		try {
			URLmessage = URLEncoder.encode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String urlString = "http://130.236.227.213:9090/sendsms?phone=0722312561&text=gest+test%202&password=123";
		String urlString = "http://" + IP + ":" + port + "/sendsms?phone=" + number + "&text=" +
				URLmessage + "&password=" + password;
		
		System.out.println(sendSMSOverHTTP(urlString));
	}

	private static String sendSMSOverHTTP(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
