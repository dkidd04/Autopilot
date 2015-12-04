package com.citigroup.liquifi.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** 
 * This class has the object relational mapping to LFTestInputSteps table in 
 * test harness database.
 * @author ac26780
 *
 */
public class LFTestInputSteps implements Step<LFTag> {
	
	private static final long serialVersionUID = 1L;
	private String testID = null;
	private int actionSequence = 0;
	private String template = null;
	private String message = null;
	private String msgType = null;
	private char useOutputMsg = 'n';
	private String outputMsgID = null;
	private String topicID = null;
	private List<LFTag> inputTagsValueList;
	private List<LFOutputMsg> outputStepList;
	private String commonTags = null;
	private String comments = null;
	private String childrenOutputSteps = null;
	
	private List<LFOutputMsg> linkedOutputStepList;
	
	// for step link 
	private UUID tempRefactorID = null;
	
	//in memory, for clipboard only
	private String testCase;
	private String stepNum;

	public LFTestInputSteps(){
		inputTagsValueList = new ArrayList<LFTag>();
		outputStepList = new ArrayList<LFOutputMsg>();
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

	
	public char getUseOutputMsg() {
		return useOutputMsg;
	}
	
	public void setUseOutputMsg(char useOutputMsg) {
		this.useOutputMsg = useOutputMsg;
	}

	public String getOutputMsgID() {
		return outputMsgID;
	}
	
	public void setOutputMsgID(String MsgID) {
		this.outputMsgID = MsgID;
	}

	public String getTopicID() {
		return topicID;
	}
	
	public void setTopicID(String topicID) {
		this.topicID = topicID;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String te) {
		this.template = te;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public List<LFTag> getInputTagsValueList() {
		return inputTagsValueList;
	}

	public void setInputTagsValueList(List<LFTag> inputTagsValueList) {
		this.inputTagsValueList = inputTagsValueList;
	}
	
	public void addToInputTagValueList(LFTag tag){
		tag.setTestID(this.testID);
		inputTagsValueList.add(tag);
	}


	public List<LFOutputMsg> getOutputStepList() {
		return outputStepList;
	}
	
	public void addOutputStep(LFOutputMsg outputStep) {
		this.outputStepList.add(outputStep);
	}

	public void setOutputStepList(List<LFOutputMsg> outputStepList) {
		this.outputStepList = outputStepList;
	}
	
	
	public String toString(){
		return "[S]Step # " + actionSequence;
	}

	public String getCommonTags() {
		return commonTags;
	}

	public void setCommonTags(String commonTags) {
		this.commonTags = commonTags;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public List<LFOutputMsg> getLinkedOutputStepList() {
		return linkedOutputStepList;
	}

	public void setLinkedOutputStepList(List<LFOutputMsg> linkedOutputStepList) {
		this.linkedOutputStepList = linkedOutputStepList;
	}
	
	public LFTestInputSteps clone() {
		LFTestInputSteps clone = new LFTestInputSteps();
		clone.setTestID(testID);
		clone.setActionSequence(actionSequence);
		clone.setTemplate(template);
		clone.setMessage(message);
		clone.setMsgType(msgType);
		clone.setOutputMsgID(outputMsgID);
		clone.setTopicID(topicID);
		clone.setUseOutputMsg(useOutputMsg);
		clone.setCommonTags(commonTags);
		clone.setComments(comments);
		List<LFOutputMsg> cloneOutputStepList = new ArrayList<LFOutputMsg>();
		for(LFOutputMsg outputStep : outputStepList) {
			cloneOutputStepList.add(outputStep.clone());
		}		
		clone.setOutputStepList(cloneOutputStepList);
		List<LFTag> cloneInputTags = new ArrayList<LFTag>();
		for(LFTag inputTag : inputTagsValueList) {
			cloneInputTags.add(inputTag.clone());
		}
		clone.setInputTagsValueList(cloneInputTags);
		return clone;
	}
	
	public LFTestInputSteps clone(String newTestID) {
		LFTestInputSteps clone = new LFTestInputSteps();
		clone.setTestID(newTestID);
		clone.setActionSequence(actionSequence);
		clone.setTemplate(template);
		clone.setMessage(message);
		clone.setMsgType(msgType);
		clone.setOutputMsgID(outputMsgID);
		clone.setTopicID(topicID);
		clone.setUseOutputMsg(useOutputMsg);
		clone.setCommonTags(commonTags);
		clone.setComments(comments);
		List<LFOutputMsg> cloneOutputStepList = new ArrayList<LFOutputMsg>();
		for(LFOutputMsg outputStep : outputStepList) {
			cloneOutputStepList.add(outputStep.clone(newTestID));
		}		
		clone.setOutputStepList(cloneOutputStepList);
		List<LFTag> cloneInputTags = new ArrayList<LFTag>();
		for(LFTag inputTag : inputTagsValueList) {
			cloneInputTags.add(inputTag.clone(newTestID));
		}
		clone.setInputTagsValueList(cloneInputTags);
		return clone;
	}
	
	
	
	public String getTestCase() {
		return testCase;
	}


	public void setTestCase(String testCase) {
		this.testCase = testCase;
	}


	public String getStepNum() {
		return stepNum;
	}


	public void setStepNum(String stepNum) {
		this.stepNum = stepNum;
	}


	public List<LFTag> getOverwriteTags() {
		return inputTagsValueList;
	}
	
	
	public String getChildrenOutputSteps() {
		return childrenOutputSteps;
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

	public void setChildrenOutputSteps(String childrenOutputSteps) {
		this.childrenOutputSteps = childrenOutputSteps;
	}
	
	public String[] getChildrenStepsArray(){
		if(childrenOutputSteps == null || childrenOutputSteps.length() == 0){
			return new String[0];
		}
		
		return childrenOutputSteps.split(",");
	}
	
	public UUID getTempRefactorID() {
		return tempRefactorID;
	}

	public void setTempRefactorID(UUID tempRefactorID) {
		this.tempRefactorID = tempRefactorID;
	}

	@Override
	public List<LFTag> getTags() {
		return inputTagsValueList;
	}
	
	
}
