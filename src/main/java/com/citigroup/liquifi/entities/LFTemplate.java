package com.citigroup.liquifi.entities;

public class LFTemplate {
	
	private String appName;
	private String templateName;
	private String msgType;
	private String msgTemplate;
	private String description;
	private String commonOverwriteTagListName;
	private char isInput = 'Y';
	
	public LFTemplate(){
		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgTemplate() {
		return msgTemplate;
	}

	public void setMsgTemplate(String msgTemplate) {
		this.msgTemplate = msgTemplate;
	}

	public char getIsInput() {
		return isInput;
	}

	public void setIsInput(char isInput) {
		this.isInput = isInput;
	}

	public String getCommonOverwriteTagListName() {
		return commonOverwriteTagListName;
	}

	public void setCommonOverwriteTagListName(String commonOverwriteTagListName) {
		this.commonOverwriteTagListName = commonOverwriteTagListName;
	}
	

}
