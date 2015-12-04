package com.citigroup.liquifi.autopilot.controller;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.util.Status;

public enum AdminMsgNotifier {
	INSTANCE;

	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	private Status status;
	private String timezone = null;
	private ValidationObject validationObject;

	public synchronized void setup(ValidationObject validationObject) {
		this.validationObject = validationObject;
	}
	
	public synchronized void reset(ValidationObject validationObject) {
		this.status = Status.NO_REPLY;
	}
	
	public synchronized Status getStatus() {
		return status;
	}

	public synchronized void processAdminMsg(String strOutputMsg, String strTopic) {
		// some systems might not be sending status
		if(strOutputMsg.contains("<Symbol>") && strOutputMsg.contains("<Status>")) {
			int start = strOutputMsg.indexOf("<Symbol>");
			int end = strOutputMsg.indexOf("</", start);
			
			String symbol = strOutputMsg.substring(start+8, end);
			
			if(validationObject.getAcceptingSymbols().contains(symbol)) {
				start = strOutputMsg.indexOf("<Status>");
				end = strOutputMsg.indexOf("</", start);
				
				if(Boolean.valueOf(strOutputMsg.substring(start+8, end))) {
					status = Status.SUCCESS; 
					
					setTimezone(strOutputMsg);
				} else {
					status = Status.FAIL;
				}
			} else {
				logger.warning("IGNOREMSG|Symbol:" + symbol);
			}
			
		} else {
			status = Status.SUCCESS; 
		}
		
		if(status != Status.NO_REPLY) {
			notifyAll();
		}
	}
	
	/*
	 * NOTE: this is a terrible way to work out the timezone of the process, but it is a quick hack.
	 * In the future there should be a better way to communicate between the process and AP to find out the timezone.
	 */
	public synchronized void setTimezone(String msg) {
		int start = msg.indexOf("<Reason>");
		if (start > -1) {
			int end = msg.indexOf("</", start);
			timezone = msg.substring(start+8, end);
		}
	}
	
	public synchronized String getTimezone() {
		return this.timezone;
	}
	
	
	public synchronized void waitForAdminMsg() throws InterruptedException {
		if (status != Status.NO_REPLY) {
			return;
		}
		
		logger.info("Start waiting for admin reply messages");
		wait(ApplicationContext.getConfig().getValidateTimeout());
		logger.info("Finished waiting for admin reply messages");
	}
}
