package com.citigroup.liquifi.autopilot.controller;

import java.util.ArrayList;
import java.util.List;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.messaging.ConnectionManager;

public enum OutputMsgNotifier {
	INSTANCE;

	private boolean waitOver = false;
	private int expectedOutputMsgNumber = 0;
	private int step = 0;
	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	private ValidationObject validationObject = null;
	private List<Message> waiting = new ArrayList<Message>();
	
	public synchronized void setup(ValidationObject validationObject) {
		this.validationObject = validationObject;
	}
	
	public synchronized boolean reset(int step, int expectedOutputMsgNumber) {
		// ignore messages from previous testcase
		if(step == 1) {
			waiting.clear();
		}
		
		this.waitOver = false;
		this.step = step;
		this.expectedOutputMsgNumber = expectedOutputMsgNumber;
		this.validationObject.setupStep(step, expectedOutputMsgNumber);
		
		if(ApplicationContext.getConfig().isCompletenessCheck()) {
			if(!waiting.isEmpty()) {
				for(Message msg : waiting) {
					validationObject.storeCurrentOutput(step, msg.message, msg.topic);
				}

				return false;
			}
		}
		
		return true;
	}
	
	public synchronized void clearWaitingSteps(){
		waiting.clear();
	}
	
	public synchronized void processOutputMsg(String strOutputMsg, String strTopic) {
		String strSymbol = ApplicationContext.getFIXFactory().getTagValue(strOutputMsg, "55");
		String topicID = ConnectionManager.INSTANCE.getAcceptorTopicId(strTopic);
		
		//do not save message if topic is not active
		if(topicID != null && !ApplicationContext.getTopicManagerTableModel().isActiveTopic(topicID)){
			return;
		}
		
		if (validationObject == null || !validationObject.getAcceptingSymbols().contains(strSymbol)) {
			logger.warning("IGNOREMSG|Symbol:" + strSymbol);
			return;
		} else {
			if(waitOver) {
				waiting.add(new Message(strOutputMsg, strTopic, 0));
			} else {
				if (validationObject.storeCurrentOutput(step, strOutputMsg, strTopic)) {
					if (expectedOutputMsgNumber <= 1) {
						expectedOutputMsgNumber = 0;

						notifyAll();
					} else {
						expectedOutputMsgNumber -= 1;
					}
				}
			}
		}
	}
	
	public synchronized void waitForAllOutputMsg() throws InterruptedException {
		if (expectedOutputMsgNumber > 0) {
			logger.info("Start waiting for output messages");
			wait(ApplicationContext.getConfig().getValidateTimeout());
			logger.info("Finished waiting for output messages");
		}
		
		waitOver = true;
	}
}
