package com.citigroup.liquifi.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
 
public class LFOutputMsg implements Step<LFOutputTag> {

	private static final long serialVersionUID = 1L;
	private int outputMsgID ;
	private int actionSequence = 0;
	private String testID = null;
	private String template = null;
	private String outputMsg = null;
	private String topicID = null;
	private String custValidationClass = null;
	private String msgType = null;
	private String commonTags = null;
	private String childrenOutputSteps = null;
	private String parentRefStep = null;
	private String parentPlaceHolder = null;
	private List<LFOutputTag> outputTagList;
	//each output step almost always contain < 3 links, don't need to use HashMap
	private UUID tempRefactorID = null; // for step link

	public LFOutputMsg(){
		outputTagList = new ArrayList<LFOutputTag>();
		//stepLinkEntries = new ArrayList<LFStepLinkEntry>();
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
	
	
	public String getOutputMsg() {
		return outputMsg;
	}
	
	public void setOutputMsg(String outputMsg) {
		this.outputMsg = outputMsg;
	}


	public List<LFOutputTag> getOutputTagList() {
		return outputTagList;
	} 
	
	public void addToOutputTagList(LFOutputTag outputTag){
		outputTag.setTestID(this.testID);
		outputTagList.add(outputTag);
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getTopicID() {
		return topicID;
	}

	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}

	public void setOutputTagList(List<LFOutputTag> outputTagList) {
		this.outputTagList = outputTagList;
	}


	public int getOutputMsgID() {
		return outputMsgID;
	}


	public void setOutputMsgID(int outputMsgID) {
		this.outputMsgID = outputMsgID;
	}

	public String getCustValidationClass() {
		return custValidationClass;
	}


	public void setCustValidationClass(String custValidationClass) {
		this.custValidationClass = custValidationClass;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgType() {
		return "FixMsg";
	}

	public String getCommonTags() {
		return commonTags;
	}

	public void setCommonTags(String commonTags) {
		this.commonTags = commonTags;

	}
	
//	public List<LFStepLinkEntry> getStepLinkEntries() {
//		return stepLinkEntries;
//	}
//
//	public void setStepLinkEntries(List<LFStepLinkEntry> stepLinkEntries) {
//		this.stepLinkEntries = stepLinkEntries;
//	}
	
	
	public LFOutputMsg clone() {
		LFOutputMsg clone = new LFOutputMsg();
		clone.setActionSequence(actionSequence);
		clone.setTestID(testID);
		clone.setCommonTags(commonTags);
		clone.setOutputMsg(outputMsg);
		clone.setOutputMsgID(outputMsgID);
		clone.setTopicID(topicID);
		clone.setTemplate(template);
		clone.setMsgType(msgType);

		List<LFOutputTag> cloneInputTags = new ArrayList<LFOutputTag>();
		for(LFOutputTag inputTag : outputTagList) {
			cloneInputTags.add(inputTag.clone());
		}
		clone.setOutputTagList(cloneInputTags);
		return clone;
	}
	
	public LFOutputMsg clone(String newTestID) {
		LFOutputMsg clone = new LFOutputMsg();
		clone.setActionSequence(actionSequence);
		clone.setTestID(newTestID);
		clone.setCommonTags(commonTags);
		clone.setOutputMsg(outputMsg);
		clone.setOutputMsgID(outputMsgID);
		clone.setTopicID(topicID);
		clone.setTemplate(template);
		clone.setMsgType(msgType);

		List<LFOutputTag> cloneInputTags = new ArrayList<LFOutputTag>();
		for(LFOutputTag inputTag : outputTagList) {
			cloneInputTags.add(inputTag.clone(newTestID));
		}
		clone.setOutputTagList(cloneInputTags);
		return clone;
	}


	public List<LFOutputTag> getOverrideTags() {
		return outputTagList;
	}


	public String getMessage() {
		return outputMsg;
	}
	
	public String getChildrenOutputSteps() {
		return childrenOutputSteps;
	}

	public void setChildrenOutputSteps(String childrenOutputSteps) {
		this.childrenOutputSteps = childrenOutputSteps;
	}

	public String getParentRefStep() {
		return parentRefStep;
	}

	public void setParentRefStep(String parentRefStep) {
		this.parentRefStep = parentRefStep;
	}
	
	public void addChildOutputStep(String childStep){
		if(this.childrenOutputSteps == null || this.childrenOutputSteps.length() == 0){
			this.childrenOutputSteps = childStep;
		}else{
			if(!this.childrenOutputSteps.contains(childStep)){
				this.childrenOutputSteps = this.childrenOutputSteps + "," + childStep;
			}
		}
	}
	
	public String[] getChildrenStepsArray(){
		if(childrenOutputSteps == null || childrenOutputSteps.length() == 0){
			return new String[0];
		}
		
		return childrenOutputSteps.split(",");
	}

	public String getParentPlaceHolder() {
		return parentPlaceHolder;
	}

	public void setParentPlaceHolder(String parentPlaceHolder) {
		this.parentPlaceHolder = parentPlaceHolder;
	}
	
	public UUID getTempRefactorID() {
		return tempRefactorID;
	}

	public void setTempRefactorID(UUID tempRefactorID) {
		this.tempRefactorID = tempRefactorID;
	}
	
	@Override
	public String toString() {
		return "[" + actionSequence + "][" + outputMsgID + "]";
	}

	@Override
	public List<LFOutputTag> getTags() {
		return outputTagList;
	}
}
