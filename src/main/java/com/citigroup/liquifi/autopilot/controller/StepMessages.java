package com.citigroup.liquifi.autopilot.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepMessages {
	Message inbound;
	List<Message> expectedOrdering = new ArrayList<Message>();
	Map<String, List<Message>> expectedMessages = new HashMap<String, List<Message>>();
	List<Message> actualOrdering = new ArrayList<Message>();
	Map<String, List<Message>> actualMessages = new HashMap<String, List<Message>>();
	private int steps;
	
	
	public StepMessages(int steps) {
		this.steps = steps;
	}
	
	public void addActual(String messageStr, String topic) {
		add(actualOrdering, actualMessages, messageStr, topic);
	}
	
	public void addExpected(String messageStr, String topic) {
		add(expectedOrdering, expectedMessages, messageStr, topic);
	}
	
	private void add(List<Message> ordering, Map<String, List<Message>> messages, String messageStr, String topic) {
		Message message = new Message(messageStr, topic, steps);
		
		ordering.add(message);
		
		if(!messages.containsKey(topic)) {
			messages.put(topic, new ArrayList<Message>());
		}
		
		messages.get(topic).add(message);
	}
	
	public String getActualMessage(int id) {
		for(Message msg : actualOrdering) {
			if(msg.assigned == id-1) {
				return msg.message;
			}
		}

		return null;
	}
	
	public List<Message> getActualMessages(String topic) {
		return actualMessages.get(topic);
	}
	
	public void setInbound(String str, String topic) {
		this.inbound = new Message(str, topic, 1);
	}
	
	public Message getInbound() {
		return inbound;
	}
	
	Comparator<Message> comparator = new Comparator<Message>() {
	    public int compare(Message c1, Message c2) {
	        return c1.assigned-c2.assigned;
	    }
	};
	
	public List<Message> getActualSortedMessages() {
		List<Message> newList = new ArrayList<Message>(actualOrdering);
		Collections.sort(newList, comparator);
		return newList;
	}
	
	public List<Message> getActualMessages() {
		return actualOrdering;
	}
	
	public List<Message> getExpectedMessages() {
		return expectedOrdering;
	}
}