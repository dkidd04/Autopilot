package com.citigroup.liquifi.autopilot.messaging;

import java.io.Serializable;

import javax.jms.TextMessage;

import com.citigroup.get.quantum.messaging.MessageListener;
import com.citigroup.get.quantum.messaging.RequestMessage;
import com.citigroup.get.quantum.messaging.RequestResponseMessage;
import com.tibco.tibjms.TibjmsTextMessage;

import com.citigroup.liquifi.autopilot.controller.AdminMsgNotifier;
import com.citigroup.liquifi.autopilot.controller.OutputMsgNotifier;
import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.util.Util;

public class AutoPilotListener implements MessageListener {
	private AceLogger logger = AceLogger.getLogger(this.getClass().getSimpleName());
	private String destination;

	public AutoPilotListener(String destination)	{	
		this.destination = destination;
	}

	@Override
	public void onMessage(RequestMessage rm) {
		try {
			String msg = (String)rm.getBody();
			logger.info("INBOUNDXML|"+destination+"|"+msg);
	
			AdminMsgNotifier.INSTANCE.processAdminMsg(msg, destination);
		} catch (Exception e) {
			logger.severe(Util.getStackTrace(e));
		}
	}

	@Override
	public void onMessage(RequestResponseMessage rrm) {
		try {
			logger.info("INBOUNDRRM|"+destination+"|"+rrm);
		} catch (Exception e) {
			logger.severe(Util.getStackTrace(e));
		}
	}

	@Override
	public void onMessage(Serializable arg0){
		try {
			//TibjmsTextMessage tempMsg = (TibjmsTextMessage) arg0;
			//Change to support a generic message type
			TextMessage tempMsg = (TextMessage) arg0;
			String strTemp = tempMsg.getText();
			logger.info("INBOUND|"+this.destination+"|"+strTemp);

			if(strTemp.startsWith("<AdminOrderResponse>")) {
				AdminMsgNotifier.INSTANCE.processAdminMsg(strTemp, this.destination);
			} else {
				OutputMsgNotifier.INSTANCE.processOutputMsg(strTemp, this.destination);
			}
		} catch (Exception e) {
			logger.severe(Util.getStackTrace(e));
		}
	}
}

