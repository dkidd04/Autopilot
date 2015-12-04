package com.citigroup.liquifi.entities;

public class LFTag extends Tag implements Cloneable{
	private static final long serialVersionUID = 1L;
	private String testID = "";
	private int actionSequence = 0;
	private char isFunction = 'N';
	
	public LFTag() {
		
	}
	
	public LFTag(String tagID, String TagValue){
		super(tagID, TagValue);
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

	public char getIsFunction() {
		return isFunction;
	}

	public void setIsFunction(char isFunction) {
		this.isFunction = isFunction;
	}	
	
	public LFTag clone() {
		LFTag clone = new LFTag(getTagID(), getTagValue());
		clone.setActionSequence(actionSequence);
		clone.setTestID(testID);
		return clone;
	}
	
	public LFTag clone(String newTestID) {
		LFTag clone = new LFTag(getTagID(), getTagValue());
		clone.setActionSequence(actionSequence);
		clone.setTestID(newTestID);
		return clone;
	}
}
