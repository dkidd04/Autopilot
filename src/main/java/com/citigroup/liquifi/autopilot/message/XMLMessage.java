package com.citigroup.liquifi.autopilot.message;

import java.util.HashMap;

public class XMLMessage {
	private HashMap<String, String> tagMap = new HashMap<String, String>();
	private HashMap<String, String> propertyTypeMap = new HashMap<String, String>();
	
	public HashMap<String, String> getTagMap() {
		return tagMap;
	}
	public void setTagMap(HashMap<String, String> tagMap) {
		this.tagMap = tagMap;
	}
	public HashMap<String, String> getPropertyTypeMap() {
		return propertyTypeMap;
	}
	public void setPropertyTypeMap(HashMap<String, String> propertyTypeMap) {
		this.propertyTypeMap = propertyTypeMap;
	}

}
