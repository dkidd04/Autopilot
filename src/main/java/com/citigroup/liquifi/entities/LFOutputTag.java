package com.citigroup.liquifi.entities;

public class LFOutputTag extends Tag implements Cloneable{
	private static final long serialVersionUID = 1L;
	private String testID = null;
	private int actionSequence = 0;
	private int outputMsgID = 0;

	public LFOutputTag(){
	}
	
	public LFOutputTag(String tagID, String tagValue){
		super(tagID, tagValue);
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

	public int getOutputMsgID() {
		return outputMsgID;
	}

	public void setOutputMsgID(int outputMsgID) {
		this.outputMsgID = outputMsgID;
	}
	
	public LFOutputTag clone() {
		LFOutputTag clone = new LFOutputTag(getTagID(), getTagValue());
		clone.setActionSequence(actionSequence);
		clone.setOutputMsgID(outputMsgID);
		clone.setTestID(testID);
		return clone;
	}
	
	public LFOutputTag clone(String newTestID) {
		LFOutputTag clone = new LFOutputTag(getTagID(), getTagValue());
		clone.setActionSequence(actionSequence);
		clone.setOutputMsgID(outputMsgID);
		clone.setTestID(newTestID);
		return clone;
	}
}
