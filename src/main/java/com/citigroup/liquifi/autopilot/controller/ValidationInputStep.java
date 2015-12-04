package com.citigroup.liquifi.autopilot.controller;

public class ValidationInputStep {
	private int inputStep = 0;
	private String msg = null;
	private String topic = null;
	private String msgType = null;

	public int getInputStep() {
		return inputStep;
	}

	public void setInputStep(int inputStep) {
		this.inputStep = inputStep;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public ValidationInputStep(int inputStep, String msg, String topic, String msgType) {
		this.inputStep = inputStep;
		this.msg = msg;
		this.topic = topic;
		this.msgType = msgType;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

}