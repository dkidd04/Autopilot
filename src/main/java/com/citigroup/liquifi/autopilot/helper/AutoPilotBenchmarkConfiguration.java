package com.citigroup.liquifi.autopilot.helper;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// refer benchmark.xml

public class AutoPilotBenchmarkConfiguration {
	private String benchmarkCriteria;
	private String warmupCriteria;
	private String initBookCriteria;
	private int securityClass;
	// message rate refers to messages per second
	private int messageRate;
	private int benchmarkSymbolList;
	// warmup rate means number of warmup messages to be sent
	private int warmUpMessages;
	// in milliseconds
	private int warmUpPause;


	public String getBenchmarkCriteria() {
		return benchmarkCriteria;
	}

	public void setBenchmarkCriteria(String benchmarkCriteria) {
		this.benchmarkCriteria = benchmarkCriteria;
	}

	public String getWarmupCriteria() {
		return warmupCriteria;
	}

	public void setWarmupCriteria(String warmupCriteria) {
		this.warmupCriteria = warmupCriteria;
	}

	public String getInitBookCriteria() {
		return initBookCriteria;
	}

	public void setInitBookCriteria(String initBookCriteria) {
		this.initBookCriteria = initBookCriteria;
	}

	public int getSecurityClass() {
		return securityClass;
	}

	public void setSecurityClass(int securityClass) {
		this.securityClass = securityClass;
	}


	public int getMessageRate() {
		return messageRate;
	}

	public void setMessageRate(int messageRate) {
		this.messageRate = messageRate;
	}

	public int getBenchmarkSymbolList() {
		return benchmarkSymbolList;
	}

	public void setBenchmarkSymbolList(int benchmarkSymbolList) {
		this.benchmarkSymbolList = benchmarkSymbolList;
	}

	public int getWarmUpMessages() {
		return warmUpMessages;
	}

	public void setWarmUpMessages(int warmUpMessages) {
		this.warmUpMessages = warmUpMessages;
	}
	public int getWarmUpPause() {
		return warmUpPause;
	}

	public void setWarmUpPause(int warmUpPause) {
		this.warmUpPause = warmUpPause;
	}


}
