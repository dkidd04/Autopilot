package com.citigroup.liquifi.autopilot.socket;

import com.citigroup.liquifi.autopilot.controller.OutputMsgNotifier;

public class PMMSocket extends ClientSocket {
	public void setup() {
		send("SEND_ALL");
	}
	
	public void receieve(String msg) {
		System.out.println("RECEIVED "+msg);

		if(msg.indexOf("35=")!=-1){
			OutputMsgNotifier.INSTANCE.processOutputMsg(msg, getOutbound());
		}
	}

}
