package com.citigroup.liquifi.autopilot.messaging;

import com.citigroup.get.quantum.messaging.Initiator;
import com.citigroup.get.quantum.messaging.MessagingException;
import com.citigroup.get.quantum.messaging.transport.JMSTransportInfo;

public class QueueInitiator extends Initiator{

	private final String selector;

	public QueueInitiator(JMSTransportInfo target) throws MessagingException {
		super(target);
		this.selector = target.getSelector();		
	}
	
	public String getSelector(){
		return this.selector;
	}

}
