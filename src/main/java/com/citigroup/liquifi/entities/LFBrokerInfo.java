package com.citigroup.liquifi.entities;
 
public class LFBrokerInfo {

	private String brokerName = null;
	private String brokerURL = null;
	private String encryptedUserID = null;
	private String encryptedPass = null;
	private char active = 'n';
	private String comments = null;
	
	public String getBrokerName() {
		return brokerName;
	}
	
	public void setBrokerName(String brokerName) {
		this.brokerName = brokerName;
	}
	
	public String getBrokerURL() {
		return brokerURL;
	}
	
	public void setBrokerURL(String brokerURL) {
		this.brokerURL = brokerURL;
	}

	public String getEncryptedUserID() {
		return encryptedUserID;
	}
	
	public void setEncryptedUserID(String encryptedUserID) {
		this.encryptedUserID = encryptedUserID;
	}

	public String getEncryptedPass() {
		return encryptedPass;
	}
	
	public void setEncryptedPass(String encryptedPass) {
		this.encryptedPass = encryptedPass;
	}
	
	public char getActive() {
		return active;
	}
	
	public void setActive(char active) {
		this.active = active;
	}

	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
		
	
}
