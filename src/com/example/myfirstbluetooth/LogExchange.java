package com.example.myfirstbluetooth;

public class LogExchange {
	
	private String conversation = "";
	private boolean done = false;
	
	public LogExchange(long timeStamp, String theCmd) {
		conversation = Long.toString(timeStamp) + "," + theCmd;
		done = false;
	}
	
	public void addResponse(long timeStamp, String response) {
		if (!done) {
			conversation += "," + Long.toString(timeStamp) + "," + response;
		} else {
			conversation = ",," + Long.toString(timeStamp) + "," + response;
		}
	}

	public void finalize() {
		conversation += "\r";
		done = true;
	}
	
	public String getExchange() {
		if (done) {
			return conversation;
		} else {
			return "";
		}
	}
}
