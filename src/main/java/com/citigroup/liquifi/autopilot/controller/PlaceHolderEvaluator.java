package com.citigroup.liquifi.autopilot.controller;

import java.util.Map;

import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.util.PlaceHolders;
import com.citigroup.liquifi.util.AutoPilotConstants;

class PlaceHolderEvaluator {

	private final PlaceHolders PLACE_HOLDERS;
	
	public PlaceHolderEvaluator(){
		PLACE_HOLDERS = ApplicationContext.getPlaceHolders();
	}

	public PlaceHolderEvaluator(PlaceHolders placeHolders){
		PLACE_HOLDERS = placeHolders;
	}
	
	public String evaluatePlaceholders(String value, ValidationObject state, Map<String, String> placeholders){
		value = replaceAPVarPlaceholder(value, placeholders); 
		return evaluateInputAndOutputPlaceholders(value, state);
	}
	
	String replaceAPVarPlaceholder(String value, Map<String, String> placeholders) {
		if (value.startsWith(AutoPilotConstants.PLACEHOLDER_APVAR)){
			String placeholder = value;
			if(value.contains(AutoPilotConstants.SEPERATOR_APVAR)){
				placeholder = value.substring(0, value.indexOf(AutoPilotConstants.SEPERATOR_APVAR));
			}
			String targetValue = placeholders.get(placeholder);
			if(targetValue!=null){
				value = value.replace(placeholder, targetValue);
			}
		}
		return value;
	}

	String evaluateInputAndOutputPlaceholders(String value, ValidationObject state) {
		String newValue = value;
		if (value.startsWith(AutoPilotConstants.PLACEHOLDER_INPUT)){
			newValue = PLACE_HOLDERS.parsePlaceHolderInput("", state.getInputStepNumber(), state, value);
		} else if (value.startsWith(AutoPilotConstants.PLACEHOLDER_OUTPUT)){
			newValue = PLACE_HOLDERS.parsePlaceHolderOutput("", state.getInputStepNumber(), value, state);
		}
		return newValue;
	}

}