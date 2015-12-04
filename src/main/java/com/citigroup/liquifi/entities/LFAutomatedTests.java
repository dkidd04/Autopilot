package com.citigroup.liquifi.entities;
 
/**
 * This class has the object relational mapping to LFAutomatedTests table in 
 * test harness database.
 * @author ac26780
 *
 */
public class LFAutomatedTests {

	private String testID = null;
	private String testCategory = null;
	private String testDesc = null;
	private String appName = null;
	private String region = null;
	private String releaseNumber = null;
	private char   active = 'n';
	
	public LFAutomatedTests () {}
	
	public String getTestID() {
		return testID;
	}
	
	public void setTestID(String testID) {
		this.testID = testID;
	}
	
	public String getTestCategory() {
		return testCategory;
	}
	
	public void setTestCategory(String testCategory) {
		this.testCategory = testCategory;
	}
	
	public String getTestDesc() {
		return testDesc;
	}
	
	public void setTestDesc(String testDesc) {
		this.testDesc = testDesc;
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

	public String getReleaseNumber() {
		return releaseNumber;
	}
	
	public void setReleaseNumber(String releaseNumber) {
		this.releaseNumber = releaseNumber;
	}

	public char getActive() {
		return active;
	}
	
	public void setActive(char active) {
		this.active = active;
	}
	
}
