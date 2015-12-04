package com.citigroup.liquifi.entities;

import java.io.Serializable;

/** 
 * updated @ ls97588
 */
public class LFTestOutcome implements Serializable{

	private static final long serialVersionUID = 1L;
	private String testID = null;
	private String lastActivityUserID = null;
	private java.util.Date validationTimestamp = null;
	private String symbol = null;
	private String validationResultStatus = null;
	private String validationObjectDetail = null;
	private String validationResultMsg = null;
	private int failedInputStep = 0;
	private int failedOutputMsgID = 0;
	private String comments = null;
	private String testingBroker = null;
	
	public int getFailedInputStep() {
		return failedInputStep;
	}
	public void setFailedInputStep(int failedInputStep) {
		this.failedInputStep = failedInputStep;
	}
	public int getFailedOutputMsgID() {
		return failedOutputMsgID;
	}
	public void setFailedOutputMsgID(int failedOutputMsgID) {
		this.failedOutputMsgID = failedOutputMsgID;
	}
	public String getLastActivityUserID() {
		return lastActivityUserID;
	}
	public void setLastActivityUserID(String lastActivityUserID) {
		this.lastActivityUserID = lastActivityUserID;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getTestID() {
		return testID;
	}
	public void setTestID(String testID) {
		this.testID = testID;
	}
	public String getValidationObjectDetail() {
		return validationObjectDetail;
	}
	public void setValidationObjectDetail(String validationObjectDetail) {
		this.validationObjectDetail = validationObjectDetail;
	}
	public String getValidationResultMsg() {
		return validationResultMsg;
	}
	public void setValidationResultMsg(String validationResultMsg) {
		this.validationResultMsg = validationResultMsg;
	}
	public String getValidationResultStatus() {
		return validationResultStatus;
	}
	public void setValidationResultStatus(String validationResultStatus) {
		this.validationResultStatus = validationResultStatus;
	}
	public java.util.Date getValidationTimestamp() {
		return validationTimestamp;
	}
	public void setValidationTimestamp(java.util.Date validationTimestamp) {
		this.validationTimestamp = validationTimestamp;
	}
	public String getTestingBroker() {
		return testingBroker;
	}
	public void setTestingBroker(String testingBroker) {
		this.testingBroker = testingBroker;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
