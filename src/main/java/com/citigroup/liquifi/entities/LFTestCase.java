package com.citigroup.liquifi.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

public class LFTestCase implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	private String appName;
	private String testID;
	private String name;
	private String description;
	private String category;
	private String region;
	private String releaseNum; 
	private String jiraNum;
	private String lastEditedUser;

	

	private char active = 'Y';
	private int securityClass;
	private List<LFTestInputSteps> inputStepList;
	private Set<LFLabel> labelSet = new HashSet<LFLabel>();

	public LFTestCase(){
		inputStepList = new ArrayList<LFTestInputSteps>();
	}

	public List<LFTestInputSteps> getInputStep() {
		return inputStepList;
	}
	
	public void addToInputStep(LFTestInputSteps inputStep){
		inputStep.setTestID(this.testID);
		inputStepList.add(inputStep);
	}

	public void setInputStep(List<LFTestInputSteps> inputStepList) {
		this.inputStepList = inputStepList;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getReleaseNum() {
		return releaseNum;
	}

	public void setReleaseNum(String releaseNum) {
		this.releaseNum = releaseNum;
	}

	public char getActive() {
		return active;
	}

	public void setActive(char active) {
		this.active = active;
	}
	
	public List<LFTestInputSteps> getInputStepList() {
		return inputStepList;
	}

	public void setInputStepList(List<LFTestInputSteps> inputStepList) {
		this.inputStepList = inputStepList;
	}

	public void setTestID(String testID) {
		this.testID = testID;
	}

	public String getTestID() {
		return testID;
	}

	public int getSecurityClass() {
		return securityClass;
	}

	public void setSecurityClass(int securityClass) {
		this.securityClass = securityClass;
	}
	
	public Set<LFLabel> getLabelSet() {
		return labelSet;
	}

	public void setLabelSet(Set<LFLabel> labelSet) {
		this.labelSet = labelSet;
	}
	
	public String getJiraNum() {
		if (jiraNum == null) {
			// default to NA
			return "NA";
		} else {
			return jiraNum;
		}
	}

	public void setJiraNum(String jiraNum) {
		this.jiraNum = jiraNum;
	}
	
	public String getLastEditedUser() {
		return lastEditedUser;
	}

	public void setLastEditedUser(String lastEditedUser) {
		this.lastEditedUser = lastEditedUser;
	}
	
	public LFTestCase clone(String newID) {
		LFTestCase clone = new LFTestCase();
		clone.setAppName(appName);
		clone.setTestID(newID);
		clone.setName(name);
		clone.setDescription(description);
		clone.setCategory(category);
		clone.setRegion(region);
		clone.setReleaseNum(releaseNum);
		clone.setActive(active);
		clone.setSecurityClass(securityClass);
		clone.setJiraNum(jiraNum);
		clone.setLastEditedUser(lastEditedUser);
		
		for(LFTestInputSteps inputStep : inputStepList){
			LFTestInputSteps cloneInputStep = inputStep.clone();
			cloneInputStep.setTestID(newID);
			clone.getInputStepList().add(cloneInputStep);

			for(LFTag cloneTag : cloneInputStep.getInputTagsValueList()){
				cloneTag.setTestID(newID);
			}

			for(LFOutputMsg cloneOutput : cloneInputStep.getOutputStepList()){
				cloneOutput.setTestID(newID);

				for(LFOutputTag cloneTag : cloneOutput.getOutputTagList()){
					cloneTag.setTestID(newID);
				}
			}
		}
		
		return clone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
	public boolean containsLabel(String label){
		LFLabel lb = new LFLabel(label);
		return this.labelSet.contains(lb);
	}
	
	@Override
	public int hashCode(){
		return this.testID.hashCode();
	}

	@Override
	public boolean equals(Object obj){
		
		if(obj == null) return false;
		
		if(obj == this) return true;
		
		
		if(!(obj instanceof LFTestCase)){
			return false;
		}
		
		return this.getTestID().equals(((LFTestCase) obj).getTestID());
	}
}
