package com.citigroup.liquifi.autopilot.helper;


import java.util.HashMap;
import java.util.Map;

import com.citigroup.liquifi.autopilot.messaging.AutoPilotBrokerInfoFactory;

public class AutoPilotMessagingConfiguration {
	private Map<String, AutoPilotBrokerInfoFactory> brokerFactoryMap = new HashMap<String, AutoPilotBrokerInfoFactory>();

	public Map<String, AutoPilotBrokerInfoFactory> getBrokerFactoryMap() {
		return brokerFactoryMap;
	}

	public void setBrokerFactoryMap(
			Map<String, AutoPilotBrokerInfoFactory> brokerFactoryMap) {
		this.brokerFactoryMap = brokerFactoryMap;
	}

}
