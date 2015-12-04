package com.citigroup.liquifi.autopilot.controller;

import com.citigroup.liquifi.autopilot.util.ValidationResult;

public class Message {
	public static int UNASSIGNED = Integer.MAX_VALUE;

	public String message;
	public String topic;
	ValidationResult[] state;
	public int assigned = Message.UNASSIGNED;
	
	Message(String message, String topic, int steps) {
		this.message = message;
		this.state = new ValidationResult[steps];
		this.topic = topic;
		
		for(int i = 0; i < steps; i++) {
			this.state[i] = null;
		}
	}
}