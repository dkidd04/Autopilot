package com.citigroup.liquifi.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class LFLabel implements Serializable, Cloneable{
	
	private static final long serialVersionUID = 1L;
	private String label;
	private String appName;
	private String region;
	private String labelID;
	
	public Set<LFTestCase> testcases = new HashSet<LFTestCase>();


	public LFLabel(){
	}
	
	public LFLabel(String labelName){
		this.label = labelName;
		String strApplication = System.getProperty("application");
		if (strApplication!=null && strApplication.trim().length()>0) {
			if (strApplication.equalsIgnoreCase("AEE")) {
				strApplication="LIQUIFI";				
			}
		}
		this.appName = strApplication;
		this.region = System.getProperty("region");
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public Set<LFTestCase> getTestcases(){
		return this.testcases;
	}
	
	public void setTestcases(Set<LFTestCase> testcases){
		this.testcases = testcases;
	}
	
	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
	public String getLabelID() {
		return labelID;
	}

	public void setLabelID(String labelID) {
		this.labelID = labelID;
	}
	
	@Override
	public int hashCode(){
		return this.label.hashCode();
	}

	@Override
	public boolean equals(Object obj){
		
		if(obj == null) return false;
		
		if(obj == this) return true;
		
		
		if(!(obj instanceof LFLabel)){
			return false;
		}
		
		return this.getLabel().equals(((LFLabel) obj).getLabel());
	}
}

