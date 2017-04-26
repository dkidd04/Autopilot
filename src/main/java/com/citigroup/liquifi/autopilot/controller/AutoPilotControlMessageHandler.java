package com.citigroup.liquifi.autopilot.controller;

import com.citigroup.liquifi.autopilot.helper.AdminRequestXMLParser;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.autopilot.message.XStreamXMLFactory;
import com.citigroup.liquifi.util.AutoPilotConstants;



public class AutoPilotControlMessageHandler {
	private static AutoPilotControlMessageHandler instance = null;
	private static Object lock = new Object();
	private AceLogger logger = AceLogger.getLogger("TestCaseController");
	//private AutoPilotControlMessageParser apParser = null;
	private AdminRequestXMLParser arParser = null;
	private XStreamXMLFactory xstreamFactory;
	
	private AutoPilotControlMessageHandler () {
		//System.out.println("WARNING: this class is a singleton.");
	}
	
	public static AutoPilotControlMessageHandler getInstance(){
		if(instance == null){
			synchronized (lock) {
				if(instance == null) {
					instance = new AutoPilotControlMessageHandler();
				}
			}
			//call recover method
		}
		return instance;
	}
	
    public boolean processControlMessage(String aoreq){
    	logger.info("processControlMessage() start.");
    	
    	try {
    		/*
    		 * strArray[0] = Name, strArray[1] = Value
    		 */
    		String[] strArray = new String[2];
    		if(Boolean.getBoolean("use.xstream")) {
        		xstreamFactory = new XStreamXMLFactory();
        		strArray = xstreamFactory.getSinglePropertyForControlMessage(aoreq);
        	} else {
        		arParser = new AdminRequestXMLParser(aoreq);
        		strArray = arParser.getSinglePropertyForControlMessage();
        	}
    		
        	if (AutoPilotConstants.CONTROL_REQUEST_TYPE_PAUSE.equals(strArray[0])) {
        		int intPause = Integer.valueOf(strArray[1]);
        		Thread.sleep(intPause);
        	}
        	else {
        		logger.warning(AutoPilotConstants.ERROR_CANNOT_PROCESS_CONTROL + " CONTROL_REQUEST_TYPE:" + strArray[0]);
        		return false;
        	}
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		logger.warning(AutoPilotConstants.ERROR_CANNOT_PROCESS_CONTROL + " Msg:" + aoreq);
    		return false;
    	}
    	

    	logger.info("processControlMessage() end.");
    	return true;
	}
}
