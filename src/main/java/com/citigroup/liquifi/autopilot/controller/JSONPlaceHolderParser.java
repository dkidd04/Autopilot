package com.citigroup.liquifi.autopilot.controller;

import java.util.List;
import java.util.Map;

import com.citigroup.liquifi.autopilot.logger.AceLogger;

class JSONPlaceHolderParser {

	private static AceLogger logger =  AceLogger.getLogger(JSONPlaceHolderParser.class.getSimpleName());
	private final PlaceHolderEvaluator placeholderEvaluator;

	public JSONPlaceHolderParser() {
		this.placeholderEvaluator = new PlaceHolderEvaluator();
	}

	public JSONPlaceHolderParser(PlaceHolderEvaluator placeholderEvaluator) {
		this.placeholderEvaluator = placeholderEvaluator;
	}

	@SuppressWarnings("unchecked")
	public void parsePlaceholders(Map<String, Object> jsonMsgMap, Map<String, String> placeholders, ValidationObject state) {
		// iterate through all fields and check for any placeholders 
		for(Object entry : jsonMsgMap.keySet()){
			String key = (String) entry;
			Object value = jsonMsgMap.get(key);
			if(value instanceof Map){
				parsePlaceholders((Map<String,Object>)value, placeholders, state);
			} else if (value instanceof List){
				parsePlaceholdersList((List<?>)value, placeholders, state);
			} else if (value instanceof String && ((String) value).startsWith("@")){
				handlePlaceholder(jsonMsgMap, key, (String) value, placeholders, state);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void parsePlaceholdersList(List<?> entries, Map<String, String> placeholders, ValidationObject state) {
		for(int i=0;i<entries.size();i++){
			Object value = entries.get(i);
			if(value instanceof Map){
				parsePlaceholders((Map<String,Object>)value, placeholders, state);
			} else if (value instanceof List){
				parsePlaceholdersList((List<?>)value, placeholders, state);
			} else {
				logger.info("Found non object field on JSON List, skipping");
			}
		}
	}

	private void handlePlaceholder(Map<String, Object> jsonMsgMap, String key, String value, Map<String, String> placeholders, ValidationObject state) {
		String newValue = getPlaceholderValue(value, placeholders, state);
		if(newValue!=null){
			jsonMsgMap.put(key, newValue);
		}
	}

	private String getPlaceholderValue(String value, Map<String, String> placeholders, ValidationObject state) {
		return placeholderEvaluator.evaluatePlaceholders(value, state, placeholders);
	}

}