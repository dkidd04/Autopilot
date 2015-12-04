package com.citigroup.liquifi.entities;

public class LFCommonOverwriteTag extends Tag {
	private static final long serialVersionUID = 1L;
	private String appName = null;
	private String commonOverwriteTagListName = null;

	public LFCommonOverwriteTag(){
		
	}

	public LFCommonOverwriteTag(String tagID, String tagValue){
		super(tagID, tagValue);
	}

	public LFCommonOverwriteTag(String appName, String commonOverwriteTagListName, String tagID, String tagValue){
		super(tagID, tagValue);
		this.appName = appName;
		this.commonOverwriteTagListName = commonOverwriteTagListName;
	}	

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getCommonOverwriteTagListName() {
		return commonOverwriteTagListName;
	}

	public void setCommonOverwriteTagListName(String commonOverwriteTagListName) {
		this.commonOverwriteTagListName = commonOverwriteTagListName;
	}
}
