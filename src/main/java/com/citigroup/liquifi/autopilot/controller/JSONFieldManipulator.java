package com.citigroup.liquifi.autopilot.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.citigroup.liquifi.autopilot.logger.AceLogger;
import com.citigroup.liquifi.entities.Tag;

class JSONFieldManipulator {
	
	private static AceLogger logger =  AceLogger.getLogger(JSONFieldManipulator.class.getSimpleName());
	
	/**
	 * Supports overwriting of fields in objects
	 * Does not allow overwriting of anything other than the first object in a list
	 * Does not create a new element in list of no element exists
	 * Assumes lists always contain objects
	 * @param jsonMsgMap
	 * @param overwriteFields
	 */
	public void overwriteJSONFields(Map<?, ?> jsonMsgMap, Set<Tag> overwriteFields) {
		for(Tag field : overwriteFields){
			String[] fieldTree = fieldTree(field);
			Queue<String> fieldPath = new LinkedList<>(Arrays.asList(fieldTree));
			overwriteJSONField(jsonMsgMap, fieldPath, field.getTagValue());
		}
	}

	private String[] fieldTree(Tag field) {
		String tagID = field.getTagID();
		String[] fieldTree;
		fieldTree = tagID.split("\\.");
		return fieldTree;
	}

	private void overwriteJSONField(Map<?,?> jsonObject, Queue<String> fieldPath, String value) {
		try{
			String nextField = getNextField(fieldPath);
			if(fieldPath.isEmpty()){
				overwriteField(jsonObject, value, nextField);
				return;
			}
			boolean isListElement = "listValue".equals(fieldPath.peek());
			Map<?,?> nextObject;
			if(isListElement){
				fieldPath.poll();
				nextObject = getJSONObjectFromList(jsonObject, nextField);
			} else{
				nextObject = getJSONObject(jsonObject, nextField);
			}
			overwriteJSONField(nextObject, fieldPath, value);
		}catch (Exception e){
			logger.warning("Failed to set field " + fieldPath);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void overwriteField(Map jsonObject, String value, String nextField) {
		Object object = jsonObject.get(nextField);
		if(object!=null){
			if(object instanceof Boolean){
				jsonObject.put(nextField, Boolean.valueOf(value));
			} else if (object instanceof Double){
				jsonObject.put(nextField, Double.valueOf(value));
			} else {
				jsonObject.put(nextField, value);
			}
		} else {
			jsonObject.put(nextField, value);
		}
	}

	private Map<?,?> getJSONObject(Map<?,?> jsonObject, String fieldName){
		Object targetField = jsonObject.get(fieldName);
		return (Map<?,?>) targetField;
	}

	private Map<?,?> getJSONObjectFromList(Map<?,?> jsonObject, String fieldName){
		Object targetField = jsonObject.get(fieldName);
		List<?> fieldList = (List<?>)targetField;
		targetField = fieldList.get(0);
		return (Map<?,?>) targetField;
	}

	private String getNextField(Queue<String> fieldPath){
		return fieldPath.poll();
	}
	
}