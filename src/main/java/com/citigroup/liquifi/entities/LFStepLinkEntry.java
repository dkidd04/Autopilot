package com.citigroup.liquifi.entities;

import java.io.Serializable;

public class LFStepLinkEntry implements Serializable, Cloneable{
	
	String apVar;
	String testID;
	int outputMsgID;
	int linkedInputStepNum;
	int actionSequence;
	
	public LFStepLinkEntry(){}
	
	public LFStepLinkEntry(String apVar, String testID, int outputMsgID, int linkedInputStepNum, int actionSequence){
		
		this.apVar = apVar;
		this.testID = testID;
		this.outputMsgID = outputMsgID;
		this.linkedInputStepNum = linkedInputStepNum;
		this.actionSequence = actionSequence;
	}
	
	public int getLinkedInputStepNum() {
		return linkedInputStepNum;
	}

	public void setLinkedInputStepNum(int linkedInputStepNum) {
		this.linkedInputStepNum = linkedInputStepNum;
	}

	public LFStepLinkEntry(String apVar){
		this.apVar = apVar;
	}

	public String getTestID() {
		return testID;
	}
	
	public void setTestID(String testID) {
		this.testID = testID;
	}
	
	public int getOutputMsgID() {
		return outputMsgID;
	}
	
	public void setOutputMsgID(int outputMsgID) {
		this.outputMsgID = outputMsgID;
	}

	public String getApVar() {
		return apVar;
	}

	public void setApVar(String apVar) {
		this.apVar = apVar;
	}
	
	public int getActionSequence() {
		return actionSequence;
	}

	public void setActionSequence(int actionSequence) {
		this.actionSequence = actionSequence;
	}


}
