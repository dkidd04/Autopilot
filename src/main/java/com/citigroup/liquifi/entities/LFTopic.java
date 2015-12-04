package com.citigroup.liquifi.entities;

import java.io.Serializable;
 
public class LFTopic implements Serializable{
	private static final long serialVersionUID = 1L;
	private String brokerName = null;
	private String topicName = null;
	private String description = null;
	private String topicID = null;
	private char active = 'n';
	

	public String getBrokerName() {
		return brokerName;
	}
	
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}

	public String getTopicName() {
		return topicName;
	}
	
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public char getActive() {
		return active;
	}
	
	public void setActive(char active) {
		this.active = active;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTopicID() {
		return topicID;
	}

	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}
	
}
