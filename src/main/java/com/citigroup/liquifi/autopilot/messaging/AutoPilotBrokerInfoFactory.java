package com.citigroup.liquifi.autopilot.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoPilotBrokerInfoFactory {
	private Map<String, AutoPilotBrokerInfo> acceptorBrokerMapping = new HashMap<String,AutoPilotBrokerInfo>();
	private Map<String, AutoPilotBrokerInfo> initiatorBrokerMapping = new HashMap<String,AutoPilotBrokerInfo>();
	private Map<String, String> acceptorBrokerMap = new HashMap<String, String>();
	private Map<String, String> initiatorBrokerMap = new HashMap<String, String>();
	private Map<String, String> reverseAcceptorBrokerMap = new HashMap<String, String>();
	private String senderCompID;
	private String senderSubID;
	private Map<String, String> senderSubIDMap = new HashMap<String, String>();
	
	public String getSenderCompID() {
		return senderCompID;
	}
	public void setSenderCompID(String senderCompID) {
		this.senderCompID = senderCompID;
	}
	public String getSenderSubID() {
		return senderSubID;
	}
	public void setSenderSubID(String senderSubID) {
		this.senderSubID = senderSubID;
	}
	
	public void setSenderSubIDMapping(Map<String, Map<String, String>> map) {
		for(String senderCompID : map.keySet()) {
			Map<String, String> senderCompIDMap = map.get(senderCompID);
			
			for(String letterRange : senderCompIDMap.keySet()) {
				String senderSubID = senderCompIDMap.get(letterRange);
				
				if(letterRange.contains("-")) {
					char first = letterRange.charAt(0);
					char last = letterRange.charAt(2);
					while(first != last) {
						senderSubIDMap.put(senderCompID+"_"+first, senderSubID);
						first++;
					}
					senderSubIDMap.put(senderCompID+"_"+last, senderSubID);
				} else {
					senderSubIDMap.put(senderCompID+"_"+letterRange, senderSubID);
				}
			}
		}
	}
	
	public String getSenderSubID(String senderCompID, String symbol) {
		String key = senderCompID+"_"+((Character) symbol.charAt(0)).toString().toUpperCase();
		
		if(senderSubIDMap.containsKey(key)) {
			return senderSubIDMap.get(key);
		}
		
		return "";
	}

	public Map<String, AutoPilotBrokerInfo> getAcceptorBrokerMapping() {
		return acceptorBrokerMapping;
	}
	
	public void setAcceptorBrokerMapping(Map<String, AutoPilotBrokerInfo> acceptorBrokerMapping) {
		this.acceptorBrokerMapping = acceptorBrokerMapping;
		
		reverseAcceptorBrokerMap.clear();
		for(Entry<String, AutoPilotBrokerInfo> entry : acceptorBrokerMapping.entrySet()) {
			for(String topic : entry.getValue().getSetup().values()) {
				reverseAcceptorBrokerMap.put(topic, entry.getKey());
			}
		}
		
		map2BiggerMap(acceptorBrokerMapping, acceptorBrokerMap);
		map2ReverseBiggerMap(acceptorBrokerMapping, reverseAcceptorBrokerMap);
		System.out.println("[setAcceptorBrokerMapping]");
	}
	
	public Map<String, AutoPilotBrokerInfo> getInitiatorBrokerMapping() {
		return initiatorBrokerMapping;
	}
	public void setInitiatorBrokerMapping(Map<String, AutoPilotBrokerInfo> initiatorBrokerMapping) {
		this.initiatorBrokerMapping = initiatorBrokerMapping;
		map2BiggerMap(initiatorBrokerMapping, initiatorBrokerMap);
	}
	
	// Map consists of topicName_symbol => topic, for example: LiqFiToCS_G => EU.LIQTOCS.4
	private void map2BiggerMap(Map<String, AutoPilotBrokerInfo> mapping, Map<String, String> map) {
		Pattern pattern = Pattern.compile("^([1-9][0-9]*Y)-([1-9][0-9]*Y)$");

		for(String topicName : mapping.keySet()) {
			AutoPilotBrokerInfo broker = mapping.get(topicName);
			for(String key : broker.getSetup().keySet()) {
				if(key.contains("-")) {
					Matcher matcher = pattern.matcher(key);
					if(matcher.matches()) {
						expandBondRange(map, topicName, broker, key);
					} else {
						expandSymbolRange(map, topicName, broker, key);
					}
				} else {
					map.put(topicName+"_"+key, broker.getSetup().get(key));
				}
			}
		}
	}
	
	private void map2ReverseBiggerMap(Map<String, AutoPilotBrokerInfo> mapping, Map<String, String> map) {
		Pattern pattern = Pattern.compile("^([1-9][0-9]*Y)-([1-9][0-9]*Y)$");

		for(String topicName : mapping.keySet()) {
			AutoPilotBrokerInfo broker = mapping.get(topicName);
			for(String key : broker.getSetup().keySet()) {
				if(key.contains("-")) {
					Matcher matcher = pattern.matcher(key);
					if(!matcher.matches()) {
						reverseExpandSymbolRange(map, topicName, broker, key);
					} 
				} else {
					map.put(topicName+"_"+key, broker.getSetup().get(key));
				}
			}
		}
	}
	
	private void reverseExpandSymbolRange(Map<String, String> map, String topicName,
			AutoPilotBrokerInfo broker, String key) {
		char first = key.charAt(0);
		char last = key.charAt(2);
		while(first != last) {
			map.put(broker.getSetup().get(key)+"_"+first, topicName);
			first++;
		}
		map.put(broker.getSetup().get(key)+"_"+last, topicName);
	}
	
	private void expandSymbolRange(Map<String, String> map, String topicName,
			AutoPilotBrokerInfo broker, String key) {
		char first = key.charAt(0);
		char last = key.charAt(2);
		while(first != last) {
			map.put(topicName+"_"+first, broker.getSetup().get(key));
			first++;
		}
		map.put(topicName+"_"+last, broker.getSetup().get(key));
	}
	
	/**
	 * This handles mapping bond ranges to topics.  For example, a key range of '2Y-30Y' will associate
	 * 2Y, 3Y, 4Y, 5Y...all with the same topic value.
	 * 
	 * @param map
	 * @param topicName
	 * @param broker
	 * @param key
	 */
	private void expandBondRange(Map<String, String> map, String topicName,
		AutoPilotBrokerInfo broker, String key) {
		int first = Integer.parseInt(key.split("-")[0].replace("Y", ""));
		int last = Integer.parseInt(key.split("-")[1].replace("Y", ""));
		while(first <= last) {
			map.put(topicName+"_"+first+"Y", broker.getSetup().get(key));
			first++;
		}
	}
	
	public Map<String, String> getAcceptorBrokerMap() {
		return acceptorBrokerMap;
	}
	
	public Map<String, String> getReverseAcceptorBrokerMap() {
		return reverseAcceptorBrokerMap;
	}
	
	public Map<String, String> getInitiatorBrokerMap() {
		return initiatorBrokerMap;
	}
}
