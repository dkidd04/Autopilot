package com.citigroup.liquifi.autopilot.db.dao;

public class BrokerInfo {
	private String brokerName;
	private String brokerURL;
	private String userID;
	private String password;
	private boolean active;
	private String comments;
        
        
    public BrokerInfo(){
        
    }

    public BrokerInfo(String brokerName, String brokerURL, String userID, String password, boolean active, String comments) {
        this.brokerName = brokerName;
        this.brokerURL = brokerURL;
        this.userID = userID;
        this.password = password;
        this.active = active;
        this.comments = comments;
                
    }
	

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
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String toString(){
		String str = "|" + brokerName + "|" + brokerURL + "|" + userID + "|" + password + "|" + active + "|" + comments;
		return str;
	}

}
