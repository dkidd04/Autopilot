package com.citigroup.liquifi.entities;
/**
 * This class has the object relational mapping to LFActions table in 
 * test harness database.
 * @author ac26780
 *
 */ 
public class LFActions {

	private String actionName = null;
	private String actionDetails = null;
	private String msgTemplate = null;
	
	public String getActionName() {
		return actionName;
	}
	
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public String getActionDetails() {
		return actionDetails;
	}
	
	public void setActionDetails(String actionDetails) {
		this.actionDetails = actionDetails;
	}

	public String getMsgTemplate() {
		return msgTemplate;
	}
	
	public void setMsgTemplate(String msgTemplate) {
		this.msgTemplate = msgTemplate;
	}
}
