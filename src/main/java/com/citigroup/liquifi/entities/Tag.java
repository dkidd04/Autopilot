package com.citigroup.liquifi.entities;

import java.io.Serializable;

public class Tag implements Serializable {
	private static final long serialVersionUID = 1L;
	private String tagID = null;
	private String tagValue = null;
	
	public Tag() {
		
	}
	
	public Tag(String tagID, String TagValue){
		this.tagID = tagID;
		this.tagValue = TagValue;
	}
	
	public String getTagID() {
		return tagID;
	}

	public void setTagID(String tagID) {
		this.tagID = tagID;
	}

	public String getTagValue() {
		return tagValue;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}

	@Override public boolean equals(Object other) {
	    boolean result = false;
	    if (other instanceof Tag) {
	    	Tag arg0 = (Tag) other;
	        result = (arg0.getTagID().equals(tagID));
	    }
	    return result;
	}
	
	@Override public String toString() {
		return tagID + "=" + tagValue;
	}
	
	@Override public int hashCode(){
		return tagID.hashCode() ;
	}
}
