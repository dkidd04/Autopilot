package com.citigroup.liquifi.entities;

import java.io.Serializable;

public class LFOutputMsgId implements Serializable{
	
	private int outputMsgID ;
	private String testID = null;
	private int actionSequence = 0;
	
	public int getOutputMsgID() {
		return outputMsgID;
	}
	public void setOutputMsgID(int outputMsgID) {
		this.outputMsgID = outputMsgID;
	}
	public String getTestID() {
		return testID;
	}
	public void setTestID(String testID) {
		this.testID = testID;
	}
	public int getActionSequence() {
		return actionSequence;
	}
	public void setActionSequence(int actionSequence) {
		this.actionSequence = actionSequence;
	}

}
